package com.jflyfox.modules.admin.goodschange;

import com.jflyfox.jfinal.base.BaseModel;
import com.jflyfox.jfinal.component.annotation.ModelBind;

@ModelBind(table = "sm_goods_change")
public class SmGoodsChange extends BaseModel<SmGoodsChange> {

	private static final long serialVersionUID = 1L;
	public static final SmGoodsChange dao = new SmGoodsChange();

}