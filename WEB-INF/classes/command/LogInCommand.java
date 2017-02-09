/*
  @author 池田千鶴
  @date 2017/02/09
*/

package command;

import bean.MemberBean;

import logic.ResponseContext;
import logic.RequestContext;
import logic.WebRequestContext;

import dao.AbstractDaoFactory;
import dao.MemberDao;
import dao.OraMemberDao;

import ex.LogicException;
import ex.IntegrationException;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

/* 通常のログインチェックコマンド(topからログイン押した時) */
public class LogInCommand extends AbstractCommand {
	/* 会員表を取得し、メールアドレスとパスワードが一致する会員を検索する */
	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException{
		System.out.println("--LogInCommand--");
		
		RequestContext reqc = getRequestContext();
		boolean flag = true;
		
		try{
			/* 会員表のリストを取得 */
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			MemberDao memberdao = factory.getMemberDao();
			List memberlist = memberdao.getMembers();
			
			/* 一行ずつ検索(見づらいのでチェックは別メソッドに分離) */
			for(int i = 0; i < memberlist.size(); i++){
				MemberBean member = (MemberBean)memberlist.get(i);
				/* 入力されたメールアドレスと
					一致するメールアドレスがあった場合、パスワードをチェック */
				if(member.getMemberEmail().equals(reqc.getParameter("email"))) {
					flag = false;
					checkPassword(member, reqc);
				}
			}
			
			/* 入力されたメールアドレスに一致するメールアドレスが
				データベース内に存在しなかった場合 */
			if(flag){
				System.out.println("メールアドレスが違います");
				reqc.setSessionAttribute("login", "NG");
			}
			
			/* ここからフィルター機能に近い動作 */
			/* セッションからログイン情報を取得 */
			String login = (String)reqc.getSessionAttribute("login");
			System.out.println("login=" + login);
			
			/* 未ログイン・ログインに失敗した場合、
				再度ログイン画面へ飛ばす */
			if(login == null || "".equals(login) || "NG".equals(login)) {
				responseContext.setTarget("login");
			/* ログイン済の場合、トップページへ飛ばす */
			}else {
				responseContext.setTarget("top");
			}
			
		}catch(IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		
		return responseContext;
	}
	
	/* パスワードが正しいかチェック */
	private static void checkPassword
		(MemberBean member, RequestContext reqc) {
		/* 入力されたパスワードと、
			メールアドレスに応じたパスワードが同じ場合(ログイン成功)、
			セッションにmember_idを登録*/
		if(member.getMemberPassword().equals(reqc.getParameter("pass"))) {
			System.out.println("ログイン成功");
			reqc.setSessionAttribute("login", member.getMemberId());
		/* パスワードが違う場合(ログイン失敗)、
			セッションにログインが失敗したことを登録*/
		}else{
			System.out.println("パスワードが違います");
			reqc.setSessionAttribute("login", "NG");
		}
	}
}