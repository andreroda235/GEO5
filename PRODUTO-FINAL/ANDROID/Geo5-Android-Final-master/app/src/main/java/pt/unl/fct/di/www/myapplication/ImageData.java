package pt.unl.fct.di.www.myapplication;

public class ImageData {

    private String user_name;
    private String file_name;
    private String file_type;
    private String file_upload_date;

    public ImageData(String user_name, String file_name, String file_type, String file_upload_date) {
        this.user_name = user_name;
        this.file_name = file_name;
        this.file_type = file_type;
        this.file_upload_date = file_upload_date;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getFile_type() {
        return file_type;
    }

    public String getFile_upload_date() {
        return file_upload_date;
    }
}
