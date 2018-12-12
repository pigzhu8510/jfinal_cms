package com.jflyfox.modules.admin.member;

import com.jflyfox.component.base.BaseProjectController;
import com.jflyfox.jfinal.component.annotation.ControllerBind;
import com.jflyfox.jfinal.component.db.SQLUtils;
import com.jflyfox.modules.admin.site.TbSite;
import com.jflyfox.system.file.util.FileUploadUtils;
import com.jflyfox.util.StrUtils;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

/**
 * 会员权益信息
 * 
 * @author flyfox 2014-4-24
 */

@ControllerBind(controllerKey = "/admin/member")
public class MemberController extends BaseProjectController {

	private static final String path = "/pages/admin/member/member_";

	public void list() {
		SmMember model = getModelByAttr(SmMember.class);

		SQLUtils sql = new SQLUtils(" from sm_member t where 1=1 ");
		if (model.getAttrValues().length != 0) {
			sql.setAlias("t");
			// 查询条件
			sql.whereLike("name", model.getStr("name"));
		}
		
		// 排序
		String orderBy = getBaseForm().getOrderBy();
		if (StrUtils.isEmpty(orderBy)) {
			sql.append(" order by id desc ");
		} else {
			sql.append(" order by ").append(orderBy);
		}

		Page<SmMember> page = SmMember.dao.paginate(getPaginator(), "select t.* ", //
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
		SmMember model = SmMember.dao.findById(getParaToInt());
		setAttr("model", model);
		render(path + "view.html");
	}

	public void delete() {
		SmMember.dao.deleteById(getParaToInt());
		list();
	}

	public void edit() {
		SmMember model = SmMember.dao.findById(getParaToInt());
		setAttr("selectMember", selectDictDetail("MemberType",model.getStr("membertypes")));
		setAttr("model", model);
		render(path + "edit.html");
	}

	public void save() {
		TbSite site = getBackSite();
		UploadFile uploadImage1 = getFile("model.picture", FileUploadUtils.getUploadTmpPath(site), FileUploadUtils.UPLOAD_MAX);
		
		Integer pid = getParaToInt();
		SmMember model = getModel(SmMember.class);
		
		// 图片附件
		if (uploadImage1 != null) {
			String fileUrl = uploadHandler(site, uploadImage1.getFile(), "roll_image");
			model.set("picture", fileUrl);
		}
				
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
