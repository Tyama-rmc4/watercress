/*
  @author 池田千鶴
  @date 2017/02/02
*/

package command;

import logic.RequestContext;
import logic.ResponseContext;
import logic.WebRequestContext;

import ex.LogicException;

/* ログアウトコマンド */
public class LogOutCommand extends AbstractCommand {
	/* ログアウトボタンが押されたら、セッションに登録してある
		ログイン情報とログイン時に使用した移動先情報を削除 */
	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException{
		System.out.println("--LogOutCommand--");
		
		RequestContext reqc = getRequestContext();
		
		reqc.removeSessionAttribute("login");
		reqc.removeSessionAttribute("target");
		
		/* トップに戻る */
		responseContext.setTarget("top");
		
		return responseContext;
	}
}