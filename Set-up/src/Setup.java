
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Setup {

    public static void main (String []args) throws IOException {

        List<String> itemList = load("resource\\item_id");

        for(String item : itemList) {
            String category = returnCategory(item);

            //Change the URL with any other publicly accessible POST resource, which accepts JSON request body
            HttpURLConnection con = setPostConnection("http://localhost:9090/engines/pcrs_change/events");

            //JSON String need to be constructed for the specific resource.
            String jsonInputString = toJson(item, category);
            System.out.println(jsonInputString);
            doPost(con, jsonInputString);
            outJsonResponse(con);
        }
    }

    public static Boolean ifContain(List<String> attributeList, String item) {

        for(String obj : attributeList) {
            if(item.contains(obj)) {
                return true;
            }
        }

        return false;
    }

    public static String toJson(String item, String category){
        String jsonInputString =
                "{\n" +
                        "    \"event\": \"$set\",\n" +
                        "    \"entityType\": \"" + "item" + "\",\n" +
                        "    \"entityId\": \""+ item + "\",\n" +
                        "    \"properties\": {\n" +
                        "          \"category\":" + "[" + category + "]\n" +
                        "    }, \n" +
                        "    \"eventTime\": \""+ LocalDateTime.now()+"Z" + "\"\n" +
                        "}";

        return jsonInputString;
    }

    public static void doPost(HttpURLConnection con, String jsonInputString) throws IOException {
        try(OutputStream os = con.getOutputStream()){
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int code = con.getResponseCode();
        System.out.println(code);
    }

    public static void doGet(HttpURLConnection con) throws IOException {
        try(InputStream os = con.getInputStream()){

        } catch (IOException e) {
            e.printStackTrace();
        }

        int code = con.getResponseCode();
        System.out.println(code);
    }

    public static HttpURLConnection setPostConnection(String path) throws IOException {
        URL url = new URL (path);

        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");

        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");

        con.setDoOutput(true);
        return con;
    }

    public static HttpURLConnection setGetConnection(String path) throws IOException {
        URL url = new URL (path);

        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");

        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");

        con.setDoOutput(true);
        return con;
    }

    public static String returnCategory(String item) {
        String category = new String();
        if(ifContain(Resources.intel_1151_cpu_category, item)) {
            category = "\"intel\", " + "\"cpu\", " + "\"1151\"";
        } else if (ifContain(Resources.intel_1151_main_category, item)) {
            category = "\"intel\", " + "\"mainboard\", " + "\"1151\"";
        } else if (ifContain(Resources.intel_1200_cpu_category, item)) {
            category = "\"intel\", " + "\"cpu\", " + "\"1200\"";
        } else if (ifContain(Resources.intel_1200_main_category, item)) {
            category = "\"intel\", " + "\"mainboard\", " + "\"1200\"";
        } else if (ifContain(Resources.amd_cpu_category, item)) {
            category = "\"amd\", " + "\"cpu\", " + "\"am4\"";
        } else if (ifContain(Resources.amd_main_category, item)) {
            category = "\"amd\", " + "\"mainboard\", " + "\"am4\"";
        } else if (ifContain(Resources.nvidia_gpu_category, item)) {
            category = "\"nvidia\", " + "\"gpu\"";
        } else if (ifContain(Resources.amd_gpu_category, item)) {
            category = "\"amd\", " + "\"gpu\"";
        } else if (ifContain(Resources.ssd_category, item)) {
            category = "\"ssd\"";
        } else if (ifContain(Resources.hdd_category, item)) {
            category = "\"hdd\"";
        } else if (ifContain(Resources.psu_category, item) && !item.contains("-2x")) {
            category = "\"psu\"";
        } else {
            category = "\"ram\"";
        }
        return category;
    }

    public static void outJsonResponse(HttpURLConnection con) {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))){
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> load(String path) throws IOException {
        List<String> list = new ArrayList<>();
        BufferedReader r = new BufferedReader(new FileReader(path));
        try {
            String line;
            while ((line=r.readLine())!=null)
                list.add(line);
        } finally {
            r.close();
        }

        return list;
    }
}
