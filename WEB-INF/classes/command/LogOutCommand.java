/*
  @author �r�c���
  @date 2017/02/02
*/

package command;

import logic.RequestContext;
import logic.ResponseContext;
import logic.WebRequestContext;

import ex.LogicException;

/* ���O�A�E�g�R�}���h */
public class LogOutCommand extends AbstractCommand {
	/* ���O�A�E�g�{�^���������ꂽ��A�Z�b�V�����ɓo�^���Ă���
		���O�C�����ƃ��O�C�����Ɏg�p�����ړ�������폜 */
	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException{
		System.out.println("--LogOutCommand--");
		
		RequestContext reqc = getRequestContext();
		
		reqc.removeSessionAttribute("login");
		reqc.removeSessionAttribute("target");
		
		/* �g�b�v�ɖ߂� */
		responseContext.setTarget("top");
		
		return responseContext;
	}
}