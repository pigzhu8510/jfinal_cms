package com.jflyfox.modules.admin.points;

import com.jflyfox.jfinal.base.BaseModel;
import com.jflyfox.jfinal.component.annotation.ModelBind;

@ModelBind(table = "sys_points")
public class SysPoints extends BaseModel<SysPoints> {

	private static final long serialVersionUID = 1L;
	public static final SysPoints dao = new SysPoints();

}