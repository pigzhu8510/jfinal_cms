package com.jflyfox.modules.admin.provider;

import java.util.ArrayList;
import java.util.List;

import com.jflyfox.component.base.BaseProjectController;
import com.jflyfox.jfinal.component.annotation.ControllerBind;
import com.jflyfox.jfinal.component.db.SQLUtils;
import com.jflyfox.modules.admin.site.TbSite;
import com.jflyfox.system.dict.SysDictDetail;
import com.jflyfox.system.file.util.FileUploadUtils;
import com.jflyfox.util.StrUtils;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;


/**
 * 服务商信息
 * 
 * @author flyfox 2014-4-24
 */

@ControllerBind(controllerKey = "/admin/provider")
public class ProviderController extends BaseProjectController {

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
			if(!"-1".equals(model.getStr("providertype"))){
				sql.whereEquals("providertype", model.getStr("providertype"));
			}
			
			if(!"-1".equals(model.getStr("evaluate"))){
				sql.whereEquals("evaluate", model.getStr("evaluate"));
			}
			
			if(!"-1".equals(model.getStr("service"))){
				sql.whereEquals("service", model.getStr("service"));
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
		setAttr("selectEvaluate", selectDictDetail("EvaluateType",model.getStr("evaluate")));
		setAttr("selectService", selectDictDetail("ServiceType",model.getStr("service")));
		setAttr("selectProvider", selectDictDetail("ProviderType",model.getStr("providertype")));

		Page<SmProvider> page = SmProvider.dao.paginate(getPaginator(), "select t.*,getItemName('EvaluateType',t.evaluate) as EvaluateType,"
				+ "getItemName('ServiceType',t.service) as ServiceType,getItemName('ProviderType',t.providertype) as ProviderTypeName ", //
				sql.toString().toString());
		
		// 下拉框
		setAttr("page", page);
		setAttr("attr", model);
		render(path + "list.html");
	}
	
	public String selectDictDetail(String dicttype,String selected) {
		List<SysDictDetail> list = new ArrayList<SysDictDetail>();
		list = SysDictDetail.dao.find("select * from sys_dict_detail where dict_type='"+dicttype+"' order by detail_sort");
		StringBuffer sb = new StringBuffer();
		for (SysDictDetail dictdetail : list) {
			sb.append("<option value=\"");
			sb.append(dictdetail.getStr("detail_code"));
			sb.append("\" ");
			sb.append(dictdetail.getStr("detail_code").equals(selected) ? "selected" : "");
			sb.append(">");
			sb.append(dictdetail.getStr("detail_name"));
			sb.append("</option>");
		}
		return sb.toString();
	}
	
	public String selectDictDetail(String dicttype,String code,String selected) {
		List<SysDictDetail> list = new ArrayList<SysDictDetail>();
		if(code == null ||"".equals(code)){
			code = "******";
		}
		list = SysDictDetail.dao.find("select * from sys_dict_detail where dict_type='"+dicttype+"' and detail_code like concat('"+code+"','%') order by detail_sort");
		StringBuffer sb = new StringBuffer();
		for (SysDictDetail dictdetail : list) {
			sb.append("<option value=\"");
			sb.append(dictdetail.getStr("detail_code"));
			sb.append("\" ");
			sb.append(dictdetail.getStr("detail_code").equals(selected) ? "selected" : "");
			sb.append(">");
			sb.append(dictdetail.getStr("detail_name"));
			sb.append("</option>");
		}
		return sb.toString();
	}

	public void add() {
		render(path + "add.html");
	}

	public void view() {
		SmProvider model = SmProvider.dao.findById(getParaToInt());
		setAttr("model", model);
		setAttr("selectService", selectDictDetail("ServiceType",model.getStr("service")));
		setAttr("selectEvaluate", selectDictDetail("EvaluateType",model.getStr("evaluate")));
		render(path + "view.html");
	}

	public void delete() {
		SmProvider.dao.deleteById(getParaToInt());
		list();
	}

	public void edit() {
		SmProvider model = SmProvider.dao.findById(getParaToInt());
		setAttr("model", model);
		setAttr("selectService", selectDictDetail("ServiceType",model.getStr("service")));
		setAttr("selectEvaluate", selectDictDetail("EvaluateType",model.getStr("evaluate")));
		setAttr("selectSub", selectDictDetail("SubType",model.getStr("service"),model.getStr("sub")));
		setAttr("selectProvider", selectDictDetail("Providertype",model.getStr("providertype")));
		setAttr("selectNature", selectDictDetail("NatureType",model.getStr("nature")));
		setAttr("selectScope", selectDictDetail("ScopeType",model.getStr("scope")));
		setAttr("selectClass", selectDictDetail("ClassType",model.getStr("classtype")));
		setAttr("selectLabel", selectDictDetail("LabelType",model.getStr("label")));
		setAttr("selectCapital", selectDictDetail("CapitalType",model.getStr("capital")));
		
		render(path + "edit.html");
	}

	public void save() {
		TbSite site = getBackSite();
		UploadFile uploadImage1 = getFile("model.logo", FileUploadUtils.getUploadTmpPath(site), FileUploadUtils.UPLOAD_MAX);
		UploadFile uploadImage2 = getFile("model.license", FileUploadUtils.getUploadTmpPath(site), FileUploadUtils.UPLOAD_MAX);
		UploadFile uploadImage3 = getFile("model.aptitude", FileUploadUtils.getUploadTmpPath(site), FileUploadUtils.UPLOAD_MAX);
		
		Integer pid = getParaToInt();
		SmProvider model = getModel(SmProvider.class);
		
		// 图片附件
		if (uploadImage1 != null) {
			String fileUrl = uploadHandler(site, uploadImage1.getFile(), "roll_image");
			model.set("logo", fileUrl);
		}
		
		if (uploadImage2 != null) {
			String fileUrl = uploadHandler(site, uploadImage2.getFile(), "roll_image");
			model.set("license", fileUrl);
		}
		
		if (uploadImage3 != null) {
			String fileUrl = uploadHandler(site, uploadImage3.getFile(), "roll_image");
			model.set("aptitude", fileUrl);
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
	
	public void select() {
		String dicttype = this.getPara("dicttype");
		String service = this.getPara("service");
		List<SysDictDetail> list = new ArrayList<SysDictDetail>();
		if(service == null ||"".equals(service)){
			service = "******";
		}
		list = SysDictDetail.dao.find("select * from sys_dict_detail where dict_type='"+dicttype+"' and detail_code like concat('"+service+"','%') order by detail_sort");
		StringBuffer sb = new StringBuffer();
		for (SysDictDetail dictdetail : list) {
			sb.append("<option value=\"");
			sb.append(dictdetail.getStr("detail_code"));
			sb.append("\" ");
			sb.append(dictdetail.getStr("detail_code").equals("-1") ? "selected" : "");
			sb.append(">");
			sb.append(dictdetail.getStr("detail_name"));
			sb.append("</option>");
		}
		
		setAttr("option", sb.toString());
		this.renderJson();
	}
}
