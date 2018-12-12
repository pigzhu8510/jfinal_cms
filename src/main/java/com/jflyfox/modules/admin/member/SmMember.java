package com.jflyfox.modules.admin.member;

import com.jflyfox.jfinal.base.BaseModel;
import com.jflyfox.jfinal.component.annotation.ModelBind;

@ModelBind(table = "sm_member")
public class SmMember extends BaseModel<SmMember> {

	private static final long serialVersionUID = 1L;
	public static final SmMember dao = new SmMember();

}