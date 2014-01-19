package controllers.api;

import actions.GlobalContextHelper;
import controllers.FrontendBasicController;
import models.BackendBasicModel;
import models.LocalFile;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import views.html.index;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;

@With(GlobalContextHelper.class)
public class FileAPIController extends FrontendBasicController {

    public static final String USERFILEPATH = "." + File.separator + "userfiles" + File.separator;

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Result get(Long fileId, String filename) {
        LocalFile fileItem = LocalFile.find.byId(fileId);

        if (fileItem != null) {
            File file = new File(USERFILEPATH + fileItem.id);
            if (file.exists() && file.isFile()) {
                response().setHeader("Content-Disposition", "attachment; filename=" + filename);
                return ok(file);
            }
        }
        return notFound();
    }

    public static String getContentType(String filename)
    {
        String g = URLConnection.guessContentTypeFromName(filename);
        if( g == null)
        {
            g = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(filename);
        }
        return g;
    }

    public static Result postFileMultipart(Long fileId) throws Exception {

        LocalFile fileItem;

        if(fileId == null) {
            return badRequest();
        }

        if(fileId.equals(0L)) {
            fileItem = new LocalFile();
            fileItem.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
        }
        else {
            fileItem = LocalFile.find.byId(fileId);
            if(fileItem == null) {
                return notFound();
            }
        }

        // create user file dir if not exists
        File locationFile = new File(USERFILEPATH);
        if (!locationFile.exists()) {
            if (locationFile.mkdirs()) {
                Logger.info("Pfad \"" + USERFILEPATH + "\" erstellt");
            }
        }

        try {
            Http.MultipartFormData body = request().body().asMultipartFormData();
            // curl -F "file=@file.png"
            Http.MultipartFormData.FilePart uploadedFilePart = body.getFile("file");
            if (uploadedFilePart == null) {
                return noContent();
            }

            File file = uploadedFilePart.getFile();
            if (file == null) {
                return noContent();
            }

            fileItem.fileName = uploadedFilePart.getFilename();
                fileItem.mimetype = getContentType(file.getName());

            fileItem.save();


            File targetFile = new File(USERFILEPATH + fileItem.id);
            if (!file.renameTo(targetFile)) {
                return internalServerError("failed to save uploaded file data!");
            }

            fileItem.updateByFile(targetFile);
            fileItem.save();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return created(fileItem.id.toString());
    }


    public static Result postFileData(Long fileId) {

        LocalFile fileItem;

        if(fileId == null) {
            return badRequest();
        }

        if(fileId.equals(0L)) {
            fileItem = new LocalFile();
            fileItem.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
        }
        else {
            fileItem = LocalFile.find.byId(fileId);
            if(fileItem == null) {
                return notFound();
            }
        }

        // create user file dir if not exists
        File locationFile = new File(USERFILEPATH);
        if (!locationFile.exists()) {
            if (locationFile.mkdirs()) {
                Logger.info("Pfad \"" + USERFILEPATH + "\" erstellt");
            }
        }

        try {
            String filename = request().getHeader("Content-Disposition").split("=")[1];

            fileItem.fileName = filename;
            fileItem.save();

            File file = request().body().asRaw().asFile();
            File targetFile = new File(USERFILEPATH + fileItem.id);
            if (!file.renameTo(targetFile)) {
                return internalServerError("failed to save uploaded file data!");
            }

            fileItem.updateByFile(targetFile);
            fileItem.save();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return created(fileItem.id.toString());
    }

}
