package com.jflyfox.modules.admin.member;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.jflyfox.component.base.BaseProjectController;
import com.jflyfox.jfinal.component.annotation.ControllerBind;
import com.jflyfox.jfinal.component.db.SQLUtils;
import com.jflyfox.modules.admin.points.SysPoints;
import com.jflyfox.system.dict.SysDictDetail;
import com.jfinal.plugin.activerecord.Page;

/**
 * 会员权益信息
 * 
 * @author flyfox 2014-4-24
 */

@ControllerBind(controllerKey = "/admin/memberconfig")
public class MemberConfigController extends BaseProjectController {

	private static final String path = "/pages/admin/memberconfig/memberconfig_";

	public void list() {
		SysDictDetail model = getModelByAttr(SysDictDetail.class);

		SQLUtils sql = new SQLUtils(" from sys_dict_detail t where dict_type='MemberType' ");
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
            
            SysDictDetail model = getModelByAttr(SysDictDetail.class);
            
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
