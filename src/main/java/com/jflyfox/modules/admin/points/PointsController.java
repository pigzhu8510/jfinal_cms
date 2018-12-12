package com.jflyfox.modules.admin.points;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.jflyfox.component.base.BaseProjectController;
import com.jflyfox.jfinal.component.annotation.ControllerBind;
import com.jflyfox.jfinal.component.db.SQLUtils;
import com.jfinal.plugin.activerecord.Page;

/**
 * 积分配置表
 * 
 * @author flyfox 2014-4-24
 */
@ControllerBind(controllerKey = "/admin/points")
public class PointsController extends BaseProjectController {

	private static final String path = "/pages/admin/points/points_";

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

	public void save() {
		String data = this.getPara("data");
		
		JSONArray jsonArray = JSONArray.parseArray(data);
		 
        List<Map<String,Object>> mapListJson = (List)jsonArray;
        for (int i = 0; i < mapListJson.size(); i++) {
            Map<String,Object> obj=mapListJson.get(i);
            
            SysPoints model = getModelByAttr(SysPoints.class);
            
            for(Entry<String,Object> entry : obj.entrySet()){
                String strkey1 = entry.getKey();
                Object strval1 = entry.getValue();
                model.set(strkey1, strval1);
            }
            
            model.update();
        }
		
		setAttr("option", data);
		renderJson();
	}
}
