package com.kientruchanoi.ecommerce.payment_gateway.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kientruchanoi.ecommerce.payment_gateway.payload.request.ItemRequest;
import com.kientruchanoi.ecommerce.payment_gateway.payload.request.ZaloPayCallbackRequest;
import com.kientruchanoi.ecommerce.payment_gateway.payload.request.ZpPaymentRequest;
import com.kientruchanoi.ecommerce.payment_gateway.payload.response.ZaloGetListBankResponse;
import com.kientruchanoi.ecommerce.payment_gateway.payload.response.ZpPaymentResponse;
import com.kientruchanoi.ecommerce.payment_gateway.service.ZaloPayService;
import com.kientruchanoi.ecommerce.payment_gateway.utils.zalopay.HMACUtil;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.kientruchanoi.ecommerce.payment_gateway.utils.Constant.ZaloConfig.*;

@Service
@Slf4j
public class ZaloPayServiceImpl implements ZaloPayService {

    private static final String ZALOPAY_CALLBACK_URL = "https://viper-chief-secondly.ngrok-free.app/api/payment/callback/zalopay";

    private String keyCallback = "eG4r0GcoNtRGbO8";
    private Mac HmacSHA256;

    public ZaloPayServiceImpl() throws Exception {
        HmacSHA256 = Mac.getInstance("HmacSHA256");
        HmacSHA256.init(new SecretKeySpec(keyCallback.getBytes(), "HmacSHA256"));
    }

    @Override
    public List<ZaloGetListBankResponse> getListBank() throws URISyntaxException, IOException {
        Map<String, String> config = new HashMap<String, String>() {{
            put("appid", ZP_APPID);
            put("key1", ZP_KEY1);
            put("key2", ZP_KEY2);
            put("endpoint", ZP_GET_BANK_ENDPOINT);
        }};

        String appid = config.get("appid");
        String reqTime = Long.toString(System.currentTimeMillis());
        String data = appid + "|" + reqTime;
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, config.get("key1"), data);
        log.warn("MAC - {}", mac);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("appid", appid));
        params.add(new BasicNameValuePair("reqtime", reqTime)); // miliseconds
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(config.get("endpoint"));
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri.build());

        CloseableHttpResponse res = client.execute(get);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject result = new JSONObject(resultJsonStr.toString());
        JSONObject banksObject = result.getJSONObject("banks");

        System.out.format("returncode = %s", result.getInt("returncode"));
        System.out.format("returnmessage = %s", result.getString("returnmessage"));

        List<ZaloGetListBankResponse> responses = new ArrayList<>();
        for(String pmcid : banksObject.keySet()) {
            JSONArray banks = banksObject.getJSONArray(pmcid);
            banks.forEach(bank -> {
                log.info("TRIGGERR - {}", bank);
                Map<String, Object> mapRes = ((JSONObject) bank).toMap();
                responses.add(
                        ZaloGetListBankResponse.builder()
                                .maxAmount((Integer) mapRes.get("maxamount"))
                                .name((String) mapRes.get("name"))
                                .displayOrder((Integer) mapRes.get("displayorder"))
                                .pmcid((Integer) mapRes.get("pmcid"))
                                .minAmount((Integer) mapRes.get("minamount"))
                                .bankCode((String) mapRes.get("bankcode"))
                                .build()
                );
            });
        }
        return responses;
    }

    @Override
    public ZpPaymentResponse createOrder(ZpPaymentRequest request) throws IOException {
        Map<String, String> config = new HashMap<String, String>(){{
            put("app_id", ZP_APPID);
            put("key1", ZP_KEY1);
            put("key2", ZP_KEY2);
            put("endpoint", ZP_CREATE_ORDER_ENDPOINT);
        }};

        Random rand = new Random();
        int random_id = rand.nextInt(1000000);
        final Map embed_data = new HashMap(){{
            put("data_rieng_don_hang", "du lieu rieng cua don hang");
        }};

        ItemRequest[] items = request.getItemRequest();
        Map<String, Object>[] mapItems = new Map[1];
        for(int i = 0; i < items.length; i++) {
            ItemRequest item = request.getItemRequest()[0];
            mapItems[0] = new HashMap(){{
                put("itemid", item.getId());
                put("itemname", item.getName());
                put("itemprice", item.getPrice());
                put("itemquantity", item.getQuantity());
            }};
        }

        Map<String, Object> order = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String itemsJson = mapper.writeValueAsString(mapItems);
            order = new HashMap<String, Object>(){{
                put("app_id", config.get("app_id"));
                put("app_trans_id", getCurrentTimeString("yyMMdd" + "_" + random_id)); // translation missing: vi.docs.shared.sample_code.comments.app_trans_id
                put("app_time", System.currentTimeMillis()); // miliseconds
                put("app_user", "user123");
                put("amount", request.getAmount());
                put("description", "HAU Ecommerce - #" + random_id);
                put("bank_code", "zalopayapp"); //chỗ này hiện đang là bankcode của tài khoản ngân hàng liên kết với app demo, nếu được sử dụng thật thì nó sẽ là tài khoản admin hệ thống
                put("item", itemsJson);
                put("callback_url", ZALOPAY_CALLBACK_URL);
                put("embed_data", new JSONObject(embed_data).toString());
            }};
        } catch (JsonProcessingException e) {
            log.error("ERROR -{} ", e.getMessage());
        }

        // app_id +”|”+ app_trans_id +”|”+ appuser +”|”+ amount +"|" + app_time +”|”+ embed_data +"|" +item
        String data = order.get("app_id") +"|"+ order.get("app_trans_id") +"|"+ order.get("app_user") +"|"+ order.get("amount")
                +"|"+ order.get("app_time") +"|"+ order.get("embed_data") +"|"+ order.get("item");
        order.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, config.get("key1"), data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(config.get("endpoint"));

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : order.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        // Content-Type: application/x-www-form-urlencoded
        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject result = new JSONObject(resultJsonStr.toString());

        return ZpPaymentResponse.builder()
                .orderUrl(result.get("order_url").toString())
                .orderToken(result.get("order_token").toString())
                .returnMessage(result.get("return_message").toString())
                .subReturnMessage(result.get("sub_return_message").toString())
                .subReturnCode((Integer) result.get("sub_return_code"))
                .zpTransToken(result.get("zp_trans_token").toString())
                .returnCode((Integer) result.get("return_code"))
                .build();
    }

    @Override
    public String receiveOrderStatus(String jsonStr) {
        JSONObject result = new JSONObject();

        try {
            JSONObject cbdata = new JSONObject(jsonStr);
            String dataStr = cbdata.getString("data");
            String reqMac = cbdata.getString("mac");

            byte[] hashBytes = HmacSHA256.doFinal(dataStr.getBytes());
            String mac = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();

            // kiểm tra callback hợp lệ (đến từ ZaloPay server)
            if (!reqMac.equals(mac)) {
                // callback không hợp lệ
                result.put("return_code", -1);
                result.put("return_message", "mac not equal");
            } else {
                // thanh toán thành công
                // merchant cập nhật trạng thái cho đơn hàng
                JSONObject data = new JSONObject(dataStr);
                log.warn("update order's status = success where app_trans_id = " + data.getString("app_trans_id"));

                result.put("return_code", 1);
                result.put("return_message", "success");
            }
        } catch (Exception ex) {
            result.put("return_code", 0); // ZaloPay server sẽ callback lại (tối đa 3 lần)
            result.put("return_message", ex.getMessage());
        }

        // thông báo kết quả cho ZaloPay server
        return result.toString();
    }

    private String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }
}
