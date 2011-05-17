package common;
import java.io.Serializable;

public class CommWrapper implements Serializable {
	
	private static final long serialVersionUID = -6642748925806637475L;
	
	private boolean error = false;
	private String errorMsg = "";
	private Object data;
	
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public boolean isError() {
		return error;
	}

	public void setError(String msg) {
		this.error = true;
		this.errorMsg = msg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	@Override
	public String toString() {
		return "errorMsg: " + errorMsg + "\ndata: \n" + data;
	}	
}