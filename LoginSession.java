package jp.co.internous.sky.model.session;

import java.io.Serializable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class LoginSession implements Serializable {
 
	private static final long serialVersionUID = 7655935956182790479L;
	
	//　フィールドを設定
	private int userId;
	private int tmpUserId;
	private String userName;
	private String password;
	private boolean logined;
	
	//　getterとsetterを設定
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId ) {
		this.userId = userId;
	}
	
	public int getTmpUserId() {
		return tmpUserId;
	}
	public void setTmpUserId(int tmpUserId) {
		this.tmpUserId = tmpUserId;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean getLogined() {
		return logined;
	}
	public void setLogined(boolean logined) {
		this.logined = logined;
	}
	
}

