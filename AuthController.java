package jp.co.internous.sky.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.sky.model.domain.MstUser;
import jp.co.internous.sky.model.domain.dto.CartDto;
import jp.co.internous.sky.model.form.UserForm;
import jp.co.internous.sky.model.mapper.MstUserMapper;
import jp.co.internous.sky.model.mapper.TblCartMapper;
import jp.co.internous.sky.model.session.LoginSession;

@Controller
@RequestMapping("/sky/auth")
public class AuthController {
	
	@Autowired
	private LoginSession loginSession; 
	
	@Autowired
	private TblCartMapper tblCartMapper;
	
	@Autowired
	private MstUserMapper mstUserMapper;
	
	Gson gson = new Gson();
	
	
//  ログイン機能　
	@ResponseBody
	@RequestMapping("/login")
	public String login(@RequestBody UserForm userForm) {
		
		//　ユーザー名とパスワードで検索
		List<MstUser> users = mstUserMapper.findByUserNameAndPassword(userForm.getUserName(), userForm.getPassword());
		
		if(users != null && users.size() > 0) {
			MstUser user = users.get(0);
			if(loginSession.getTmpUserId() != 0) {
				//　仮ユーザーIDに紐づくカート情報を取得
				List<CartDto> cartDto = tblCartMapper.findByUserId(loginSession.getTmpUserId());
				//　取得したカート情報をユーザーIDに紐づけ直す 　
				if(cartDto != null && cartDto.size() > 0) {
					tblCartMapper.updateUserId(user.getId(), loginSession.getTmpUserId());
				}
			}
			
			//　ログインセッションの内容変更(ログイン成功)
			loginSession.setUserId(user.getId());
			loginSession.setTmpUserId(0);
			loginSession.setUserName(user.getUserName());
			loginSession.setPassword(user.getPassword());
			loginSession.setLogined(true);
			
		} else {
			//　ログインセッションの内容変更(ログイン失敗)
			loginSession.setUserId(0);
			loginSession.setUserName(null);
			loginSession.setPassword(null);
			loginSession.setLogined(false);
			
			users.add(0, null);
			
		}
		
		return gson.toJson(users);
	}
	
//  ログアウト機能
	@ResponseBody
	@RequestMapping("/logout")
	public String logout() {
		
		//　ログインセッションの内容変更
		loginSession.setUserId(0);
		loginSession.setTmpUserId(0);
		loginSession.setUserName(null);
		loginSession.setPassword(null);
		loginSession.setLogined(false);
		
		return gson.toJson("1");
	}
	
//  パスワード再設定機能　
	@ResponseBody
	@RequestMapping("/resetPassword")
	public String resetPassword(@RequestBody UserForm userForm) {
		
		String newPassword = userForm.getNewPassword();
		String newPasswordConfirm = userForm.getNewPasswordConfirm();
		
		//　入力されたユーザー名とパスワードから一致するユーザーを確認
		List<MstUser> users = mstUserMapper.findByUserNameAndPassword(userForm.getUserName(), userForm.getPassword());
		
		if(users != null && users.size() > 0) {
			MstUser user = users.get(0);
			//　現パスワードと新パスワードが一致していないことを確認
			if (!user.getPassword().equals(newPassword)) {
				//　入力された新パスワードと確認用の新パスワードが一致しているか確認
				if (newPassword.equals(newPasswordConfirm)) {
					//　DBとセッションのパスワードを変更
					mstUserMapper.update(newPassword, user.getPassword());
					loginSession.setPassword(newPassword);
					
					return "パスワードが再設定されました。";
					
				} else {
					return "新パスワードと確認用パスワードが一致しません。";
				}
			} else {
				return "現在のパスワードと同一文字列が入力されました。";
			}
		} else {
			return "現在のパスワードが正しくありません。";
		}
		
	}
}