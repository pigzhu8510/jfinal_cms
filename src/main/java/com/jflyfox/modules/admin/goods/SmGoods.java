package com.jflyfox.modules.admin.goods;

import com.jflyfox.jfinal.base.BaseModel;
import com.jflyfox.jfinal.component.annotation.ModelBind;

@ModelBind(table = "sm_goods")
public class SmGoods extends BaseModel<SmGoods> {

	private static final long serialVersionUID = 1L;
	public static final SmGoods dao = new SmGoods();

}