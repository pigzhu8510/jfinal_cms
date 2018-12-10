package com.jflyfox.modules.admin.provider;

import com.jflyfox.jfinal.base.BaseModel;
import com.jflyfox.jfinal.component.annotation.ModelBind;

@ModelBind(table = "sm_provider")
public class SmProvider extends BaseModel<SmProvider> {

	private static final long serialVersionUID = 1L;
	public static final SmProvider dao = new SmProvider();

}