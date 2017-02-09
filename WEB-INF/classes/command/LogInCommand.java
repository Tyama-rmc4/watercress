/*
  @author �r�c���
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

/* �ʏ�̃��O�C���`�F�b�N�R�}���h(top���烍�O�C����������) */
public class LogInCommand extends AbstractCommand {
	/* ����\���擾���A���[���A�h���X�ƃp�X���[�h����v���������������� */
	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException{
		System.out.println("--LogInCommand--");
		
		RequestContext reqc = getRequestContext();
		boolean flag = true;
		
		try{
			/* ����\�̃��X�g���擾 */
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			MemberDao memberdao = factory.getMemberDao();
			List memberlist = memberdao.getMembers();
			
			/* ��s������(���Â炢�̂Ń`�F�b�N�͕ʃ��\�b�h�ɕ���) */
			for(int i = 0; i < memberlist.size(); i++){
				MemberBean member = (MemberBean)memberlist.get(i);
				/* ���͂��ꂽ���[���A�h���X��
					��v���郁�[���A�h���X���������ꍇ�A�p�X���[�h���`�F�b�N */
				if(member.getMemberEmail().equals(reqc.getParameter("email"))) {
					flag = false;
					checkPassword(member, reqc);
				}
			}
			
			/* ���͂��ꂽ���[���A�h���X�Ɉ�v���郁�[���A�h���X��
				�f�[�^�x�[�X���ɑ��݂��Ȃ������ꍇ */
			if(flag){
				System.out.println("���[���A�h���X���Ⴂ�܂�");
				reqc.setSessionAttribute("login", "NG");
			}
			
			/* ��������t�B���^�[�@�\�ɋ߂����� */
			/* �Z�b�V�������烍�O�C�������擾 */
			String login = (String)reqc.getSessionAttribute("login");
			System.out.println("login=" + login);
			
			/* �����O�C���E���O�C���Ɏ��s�����ꍇ�A
				�ēx���O�C����ʂ֔�΂� */
			if(login == null || "".equals(login) || "NG".equals(login)) {
				responseContext.setTarget("login");
			/* ���O�C���ς̏ꍇ�A�g�b�v�y�[�W�֔�΂� */
			}else {
				responseContext.setTarget("top");
			}
			
		}catch(IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		
		return responseContext;
	}
	
	/* �p�X���[�h�����������`�F�b�N */
	private static void checkPassword
		(MemberBean member, RequestContext reqc) {
		/* ���͂��ꂽ�p�X���[�h�ƁA
			���[���A�h���X�ɉ������p�X���[�h�������ꍇ(���O�C������)�A
			�Z�b�V������member_id��o�^*/
		if(member.getMemberPassword().equals(reqc.getParameter("pass"))) {
			System.out.println("���O�C������");
			reqc.setSessionAttribute("login", member.getMemberId());
		/* �p�X���[�h���Ⴄ�ꍇ(���O�C�����s)�A
			�Z�b�V�����Ƀ��O�C�������s�������Ƃ�o�^*/
		}else{
			System.out.println("�p�X���[�h���Ⴂ�܂�");
			reqc.setSessionAttribute("login", "NG");
		}
	}
}