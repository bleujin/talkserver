package net.ion.emotion;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

import net.ion.emotion.Emotion.EType;
import net.ion.framework.util.Debug;
import net.ion.framework.util.FileUtil;
import junit.framework.TestCase;

public class TestEmotion extends TestCase{

	public void testSad() throws Exception {
		String text = ":(";
		
		EmotionalState es = Empathyscope.feel(text) ;
		Debug.line(es.getStrongestEmotion().etype(), es.getStrongestEmotion().weight(), es.getValence());
	}

	public void testSad2() throws Exception {
		String text = ":)";
		
		EmotionalState es = Empathyscope.feel(text) ;
		Debug.line(es.getStrongestEmotion().etype(), es.getStrongestEmotion().weight(), es.getValence());
	}
	
	public void testHaveAllEmotion() throws Exception {
		for(File file : new File("./resource/bot/toon/char").listFiles()){
			if (file.isDirectory()){
				for (EType etype : EType.values()) {
					File imgfile = new File(file, etype.toString().toLowerCase() + ".png") ;
					if (! imgfile.exists()) {
						Debug.line(imgfile);
					}
				} 
			}
		}
	}

}
