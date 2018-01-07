package org.bigmouth.senon.admin.controller;

import org.bigmouth.senon.admin.service.FileService;
import org.bigmouth.senon.commom.model.CommonResponse;
import org.bigmouth.senon.commom.model.FileEntity;
import org.bigmouth.senon.commom.model.FileType;
import org.bigmouth.senon.commom.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/tree")
public class TreeController {
    @Autowired
    private FileService fileService;

    @RequestMapping(value = "get")
    @ResponseBody
    public FileEntity get(Long id) {
        return fileService.findOne(id);
    }

    @RequestMapping(value = "list_files")
    @ResponseBody
    public List<TreeNode> list(Long id, String[] properties, String[] directions) {
        List<TreeNode> result = new ArrayList<TreeNode>();
        List<FileEntity> files = new ArrayList<FileEntity>();

        if (properties != null && directions != null && properties.length == directions.length) {
            String[][] orderProperties = new String[properties.length][2];
            for (int i = 0; i < properties.length; i++)
                orderProperties[i] = new String[]{properties[i], directions[i]};
            files = fileService.listFilesByParent(id, orderProperties);
        } else {
            files = fileService.listFilesByParent(id);
        }

        for (FileEntity file : files) {
            result.add(new TreeNode(file.getId(), file.getName() + "[" + file.getId() + "]", file.getFileType() == FileType.FOLDER));
        }

        return result;
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public CommonResponse save(FileEntity fileEntity) {
        Long id = null;
        try {
            id = fileService.save(fileEntity);
        } catch (Exception e) {
            return new CommonResponse(0, "保存失败", e.getMessage());
        }
        return new CommonResponse(1, "保存成功", id);
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public CommonResponse delete(Long id) {
        try {
            fileService.delete(id);
        } catch (Exception e) {
            return new CommonResponse(0, "删除失败", e.getMessage());
        }
        return new CommonResponse(1, "删除成功");
    }
}
