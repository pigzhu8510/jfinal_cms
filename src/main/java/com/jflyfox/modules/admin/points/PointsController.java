package com.jflyfox.modules.admin.points;

import com.jflyfox.jfinal.base.BaseController;
import com.jflyfox.jfinal.component.db.SQLUtils;
import com.jfinal.plugin.activerecord.Page;

/**
 * 积分配置表
 * 
 * @author flyfox 2014-4-24
 */
public class PointsController extends BaseController {

	private static final String path = "/pages/points/points_";

	public void list() {
		SysPoints model = getModelByAttr(SysPoints.class);

		SQLUtils sql = new SQLUtils(" from sys_points t where 1=1 ");
		if (model.getAttrValues().length != 0) {
			sql.setAlias("t");
			// 查询条件
		}

		Page<SysPoints> page = SysPoints.dao.paginate(getPaginator(), "select t.* ", //
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
		SysPoints model = SysPoints.dao.findById(getParaToInt());
		setAttr("model", model);
		render(path + "view.html");
	}

	public void delete() {
		SysPoints.dao.deleteById(getParaToInt());
		list();
	}

	public void edit() {
		SysPoints model = SysPoints.dao.findById(getParaToInt());
		setAttr("model", model);
		render(path + "edit.html");
	}

	public void save() {
		Integer pid = getParaToInt();
		SysPoints model = getModel(SysPoints.class);
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
