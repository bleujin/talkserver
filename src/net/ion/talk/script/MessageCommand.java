package net.ion.talk.script;

import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;

public class MessageCommand {

	private String message;
	private String remains;
	private String[] cmds;

	private MessageCommand(String message) {
		this.message = message;
		this.cmds = StringUtil.split(message, " ");
		this.remains = StringUtil.substringAfter(message, " ");
	}

	public static MessageCommand create(String message) {
		return new MessageCommand(message);
	}

	public String fnName() {
		return cmds[0];
	}

	public String remain(int index) {
		if (cmds.length <= (index + 1))
			return "";
		return StringUtil.trim(cmds[index + 1]);
	}

	public int remainAsInt(int index, int defaultValue) {
		if (cmds.length <= (index + 1))
			return defaultValue;
		return NumberUtil.toInt(StringUtil.trim(cmds[index + 1]), defaultValue);
	}

	public String remainAsString(int index, String defaultValue) {
		if (cmds.length <= (index + 1))
			return defaultValue;
		return StringUtil.defaultIfEmpty(remain(index), defaultValue);
	}

	public String remains() {
		return remains;
	}

}
