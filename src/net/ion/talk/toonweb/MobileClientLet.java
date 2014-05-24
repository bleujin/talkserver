package net.ion.talk.toonweb;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.ContextParam;
import net.ion.radon.core.let.InnerRequest;
import net.ion.talk.bean.Const;
import net.ion.talk.let.LoginLet;
import org.antlr.stringtemplate.StringTemplate;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MobileClientLet implements IServiceLet {

    @Get
    @Post
    public Representation viewPage(@ContextParam("repository") RepositoryEntry rentry, @AnRequest InnerRequest request) throws IOException, InterruptedException, ExecutionException {

        final String userId = request.getClientInfo().getUser().getIdentifier() ;
        ReadSession session = rentry.login() ;
        String websocketURI = LoginLet.targetAddress(session, userId) ;



        // net.ion.toon.aradon.ClientLet
        String fileName = "./resource/toonweb/mobile/chat.htm" ;
        File tplFile = new File(fileName);
        if (!tplFile.exists())
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "not found template file : " + fileName);

        final StringWriter template = new StringWriter();
        IOUtil.copyNClose(new FileReader(tplFile), template) ;


        String nickName = session.pathBy("/users/" + userId).property(Const.User.NickName).asString() ;
        StringTemplate st = new StringTemplate(template.toString());
        Map<String, String> configMap = MapUtil.<String>chainKeyMap().put("address", websocketURI).put("sender", userId).put("nickName", nickName).toMap() ;

        st.setAttribute("config", configMap);

        session.tran(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/connections/" + userId).property("source", "web") ;
                return null;
            }
        });



        return new StringRepresentation(st.toString(), MediaType.TEXT_HTML, Language.ALL);
    }
}