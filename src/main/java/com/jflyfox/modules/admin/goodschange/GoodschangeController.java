package com.jflyfox.modules.admin.goodschange;

import com.jflyfox.component.base.BaseProjectController;
import com.jflyfox.jfinal.component.annotation.ControllerBind;
import com.jflyfox.jfinal.component.db.SQLUtils;
import com.jflyfox.util.StrUtils;
import com.jfinal.plugin.activerecord.Page;

/**
 * 商品兑换记录
 * 
 * @author flyfox 2014-4-24
 */
@ControllerBind(controllerKey = "/admin/goodschange")
public class GoodschangeController extends BaseProjectController {

	private static final String path = "/pages/admin/goodschange/goodschange_";

	public void list() {
		SmGoodsChange model = getModelByAttr(SmGoodsChange.class);
		String name = this.getPara("name");
		String goodstype = this.getPara("goodstype");
		String phone = this.getPara("phone");

		SQLUtils sql = new SQLUtils(" from sm_goods_change t,tb_contact tb,sm_goods sg where t.customerid=tb.id and t.goodsid=sg.id ");
		if (model.getAttrValues().length != 0) {
			// 查询条件
			sql.whereLike("sg.name", name);
			if(!"-1".equals(goodstype)){
				sql.whereEquals("sg.goodstype", goodstype);
			}
			if(!"-1".equals(model.getStr("changeflag"))){
				sql.whereEquals("t.changeflag", model.getStr("changeflag"));
			}
			sql.whereLike("t.changeno", model.getStr("changeno"));
			sql.whereLike("tb.phone", phone);
		}
		
		// 排序
		String orderBy = getBaseForm().getOrderBy();
		if (StrUtils.isEmpty(orderBy)) {
			sql.append(" order by t.id desc ");
		} else {
			sql.append(" order by ").append(orderBy);
		}
		
		//查询下拉框 
		setAttr("selectGoods", selectDictDetail("GoodsType",goodstype));
		setAttr("selectChange", selectDictDetail("ChangeFlag",model.getStr("changeflag")));

		Page<SmGoodsChange> page = SmGoodsChange.dao.paginate(getPaginator(), "select sg.name,sg.goodstype,getItemName('GoodsType',sg.goodstype) as GoodsTypeName,"
				+ "tb.phone,t.changeno,t.changeflag,getItemName('ChangeFlag',t.changeflag) as ChangeFlagName,t.update_time ", //
				sql.toString().toString());

		// 下拉框
		setAttr("page", page);
		setAttr("attr", model);
		setAttr("name", name);
		setAttr("goodstype", goodstype);
		setAttr("phone", phone);
		render(path + "list.html");
	}
	
}
