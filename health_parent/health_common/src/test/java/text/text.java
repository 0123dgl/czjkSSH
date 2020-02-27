package text;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.gson.Gson;
import com.itheima.health.utils.SMSUtils;
import com.itheima.health.utils.ValidateCodeUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class text {

    @Test
    public void fun() {
//构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
//...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
        String accessKey = "OVpkVCwO-LXXlLE3qPqwcaeJ9FF-bfnyUgfYpx51";
        String secretKey = "F1I6R1bKsfVFWsVXJg3QzrYVEmaFGsxhMmK9R98E";
        String bucket = "coryetext";
//如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "C:\\win0高清护眼屏保/大山-win10护眼高清屏保.jpg";
//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }

    @Test
    public void fun1() {
//构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
//...其他参数参考类注释
        String accessKey = "your access key";
        String secretKey = "your secret key";
        String bucket = "your bucket name";
        String key = "your file key";
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }

    }

    // （1）从Excel文件读取数据（简化）

    /**
     * XSSFWorkBook：工作簿
     * XSSFSheet：工作表（从0开始）
     * XSSFRow：行对象（从0开始）
     * XSSFCell：单元格对象（从0开始）
     */
    @Test
    public void readExcel() throws IOException {
        // 1：创建工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook("D:/hello.xlsx");
        // 2：获得工作表对象
        // XSSFSheet sheet = workbook.getSheet("预约设置模板");
        XSSFSheet sheet = workbook.getSheetAt(0);
        // 3：遍历工作表对象 获得行对象
        for (Row row : sheet) { // 出现空格（空格表示多了一些无谓的行）
            // 4：遍历行对象 获得单元格（列）对象
            for (Cell cell : row) {
                // 5：获得数据（获取字符串）
                String value = cell.getStringCellValue();
                System.out.println(value);
            }
        }
        // 6：关闭
        workbook.close();
    }

    // （2）从Excel文件读取数据（简化）
    @Test
    public void readExcel_2() throws IOException {
        // 1：创建工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook("D:/hello.xlsx");
        // 2：获得工作表对象
//        XSSFSheet sheet = workbook.getSheet("预约设置模板");
        XSSFSheet sheet = workbook.getSheetAt(0);
        // 3：遍历工作表对象 获得行对象
        // 获取Sheet对象对应的最后一个行的索引
        int lastRowNum = sheet.getLastRowNum();
        System.out.println(lastRowNum);
        for (int i = 0; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            // 获取行对象对应的最后一个单元格的索引
            short cellNum = row.getLastCellNum();
            System.out.println(cellNum);
            for (int j = 0; j < cellNum; j++) {
                XSSFCell cell = row.getCell(j);
                String value = cell.getStringCellValue();
                System.out.println(value);
            }
        }
        // 6：关闭
        workbook.close();
    }

    // 使用POI可以在内存中创建一个Excel文件并将数据写入到这个文件，最后通过输出流将内存中的Excel文件下载到磁盘
    @Test
    public void writeExcel() throws IOException {
        // 1：创建工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 2：创建工作表对象
        XSSFSheet sheet = workbook.createSheet("用户信息");
        // 3：创建行对象
        XSSFRow row1 = sheet.createRow(0);
        // 4：创建单元格对象，创建数据
        row1.createCell(0).setCellValue("姓名");
        row1.createCell(1).setCellValue("年龄");
        row1.createCell(2).setCellValue("地址");

        XSSFRow row2 = sheet.createRow(1);
        // 4：创建单元格对象，创建数据
        row2.createCell(0).setCellValue("张无忌");
        row2.createCell(1).setCellValue("25");
        row2.createCell(2).setCellValue("武当山");

        // 4：创建单元格对象，创建数据
        XSSFRow row3 = sheet.createRow(2);
        row3.createCell(0).setCellValue("灭绝师太");
        row3.createCell(1).setCellValue("40");
        row3.createCell(2).setCellValue("峨眉");

        // 5：输出excel
        OutputStream out = new FileOutputStream(new File("D:/user.xlsx"));
        workbook.write(out);
        out.flush();
        out.close();
        // 6：关闭
        workbook.close();
    }

    @Test
    public void fun4() throws Exception {
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化ascClient需要的几个参数
        final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
        final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
        //替换成你的AK
        final String accessKeyId = "yourAccessKeyId";//你的accessKeyId,参考本文档步骤2
        final String accessKeySecret = "yourAccessKeySecret";//你的accessKeySecret，参考本文档步骤2
        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为国际区号+号码，如“85200000000”
        request.setPhoneNumbers("1500000000");
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("云通信");
        //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
        request.setTemplateCode("SMS_1000000");
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"123\"}");
        //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");
        //请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
        //请求成功
            System.out.println(sendSmsResponse.getCode());
        }
    }



    @Test
    public void sendSMSUtils() throws Exception {
        // 生成验证码
        Integer code6 = ValidateCodeUtils.generateValidateCode(6);
        SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,"18003941120",code6.toString());
        System.out.println(code6);
    }
}
