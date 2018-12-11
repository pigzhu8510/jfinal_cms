package com.jflyfox.modules.admin.goods;


import com.jflyfox.component.base.BaseProjectController;
import com.jflyfox.jfinal.component.annotation.ControllerBind;
import com.jflyfox.jfinal.component.db.SQLUtils;
import com.jflyfox.modules.admin.site.TbSite;
import com.jflyfox.system.file.util.FileUploadUtils;
import com.jflyfox.util.StrUtils;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;


/**
 * 商品信息表
 * 
 * @author flyfox 2014-4-24
 */
@ControllerBind(controllerKey = "/admin/goods")
public class GoodsController extends BaseProjectController {

	private static final String path = "/pages/admin/goods/goods_";

	public void list() {
		SmGoods model = getModelByAttr(SmGoods.class);

		SQLUtils sql = new SQLUtils(" from sm_goods t where 1=1 ");
		if (model.getAttrValues().length != 0) {
			sql.setAlias("t");
			// 查询条件
			sql.whereLike("name", model.getStr("name"));
			if(!"-1".equals(model.getStr("goodstype"))){
				sql.whereEquals("goodstype", model.getStr("goodstype"));
			}
		}
		
		// 排序
		String orderBy = getBaseForm().getOrderBy();
		if (StrUtils.isEmpty(orderBy)) {
			sql.append(" order by id desc ");
		} else {
			sql.append(" order by ").append(orderBy);
		}
		
		//查询下拉框 
		setAttr("selectGoods", selectDictDetail("GoodsType",model.getStr("goodstype")));

		Page<SmGoods> page = SmGoods.dao.paginate(getPaginator(), "select t.*,getItemName('GoodsType',t.goodstype) as GoodsTypeName ", //
				sql.toString().toString());

		// 下拉框
		setAttr("page", page);
		setAttr("attr", model);
		render(path + "list.html");
	}

	public void add() {
		setAttr("selectGoods", selectDictDetail("GoodsType","-1"));
		setAttr("selectGrounding", selectDictDetail("GroundingType","-1"));
		render(path + "add.html");
	}

	public void view() {
		SmGoods model = SmGoods.dao.findById(getParaToInt());
		setAttr("model", model);
		render(path + "view.html");
	}

	public void change() {
		SmGoods model = SmGoods.dao.findById(getParaToInt());
		String grounding = model.getStr("grounding");
		if("010".equals(grounding)){
			model.set("grounding", "020");
		}else if("020".equals(grounding)){
			model.set("grounding", "010");
		}
		model.update();
		list();
	}
	
	public void delete() {
		SmGoods.dao.deleteById(getParaToInt());
		list();
	}

	public void edit() {
		SmGoods model = SmGoods.dao.findById(getParaToInt());
		setAttr("selectGoods", selectDictDetail("GoodsType",model.getStr("goodstype")));
		setAttr("selectGrounding", selectDictDetail("GroundingType",model.getStr("grounding")));
		setAttr("model", model);
		render(path + "edit.html");
	}

	public void save() {
		TbSite site = getBackSite();
		UploadFile uploadImage1 = getFile("model.picture", FileUploadUtils.getUploadTmpPath(site), FileUploadUtils.UPLOAD_MAX);
		
		Integer pid = getParaToInt();
		SmGoods model = getModel(SmGoods.class);
		
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
