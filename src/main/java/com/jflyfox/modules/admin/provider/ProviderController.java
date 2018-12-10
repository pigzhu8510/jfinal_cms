package com.jflyfox.modules.admin.provider;

import com.jflyfox.jfinal.base.BaseController;
import com.jflyfox.jfinal.component.annotation.ControllerBind;
import com.jflyfox.jfinal.component.db.SQLUtils;
import com.jflyfox.util.StrUtils;
import com.jfinal.plugin.activerecord.Page;

/**
 * 服务商信息
 * 
 * @author flyfox 2014-4-24
 */

@ControllerBind(controllerKey = "/admin/provider")
public class ProviderController extends BaseController {

	private static final String path = "/pages/admin/provider/provider_";

	public void list() {
		SmProvider model = getModelByAttr(SmProvider.class);

		SQLUtils sql = new SQLUtils(" from sm_provider t where 1=1 ");
		if (model.getAttrValues().length != 0) {
			sql.setAlias("t");
			// 查询条件
			sql.whereLike("name", model.getStr("name"));
			sql.whereLike("telephone", model.getStr("telephone"));
			sql.whereLike("company", model.getStr("company"));
			sql.whereEquals("evaluate", model.getStr("evaluate"));
			sql.whereEquals("service", model.getStr("service"));
		}
		
		// 排序
		String orderBy = getBaseForm().getOrderBy();
		if (StrUtils.isEmpty(orderBy)) {
			sql.append(" order by id desc ");
		} else {
			sql.append(" order by ").append(orderBy);
		}

		Page<SmProvider> page = SmProvider.dao.paginate(getPaginator(), "select t.* ", //
				sql.toString().toString());

		// 下拉框
		setAttr("page", page);
		setAttr("attr", model);
		render(path + "list.html");
	}

	public void add() {
		render(path + "add.html");
	}

	public void view() {
		SmProvider model = SmProvider.dao.findById(getParaToInt());
		setAttr("model", model);
		render(path + "view.html");
	}

	public void delete() {
		SmProvider.dao.deleteById(getParaToInt());
		list();
	}

	public void edit() {
		SmProvider model = SmProvider.dao.findById(getParaToInt());
		setAttr("model", model);
		render(path + "edit.html");
	}

	public void save() {
		Integer pid = getParaToInt();
		SmProvider model = getModel(SmProvider.class);
		if (pid != null && pid > 0) { // 更新
			model.update();
		} else { // 新增
			model.remove("id");
			model.put("create_id", getSessionUser().getUserid());
			model.put("create_time", getNow());
			model.save();
		}
		renderMessage("保存成功");
	}
}
