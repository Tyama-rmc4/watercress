/*
  @author 宇津野光
  @date 2017/02/15
*/

package command;

import logic.RequestContext;
import logic.ResponseContext;
import logic.WebRequestContext;

import ex.LogicException;

public class ShowNonMemberOrderCommand extends AbstractCommand {
	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException{
		
		responseContext.setTarget("nonmemberorder");
		
		return responseContext;
	}
}