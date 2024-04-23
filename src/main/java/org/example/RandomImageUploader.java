package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class RandomImageUploader {

    public static void main(String[] args) {
        // Путь к директории с изображениями
        String directoryPath = "/Users/n.guk/Desktop/котята";

        // URL сервера, куда будет отправлен POST запрос
        String serverUrl = "https://time.tinkoff.ru/api/v4/files";

        // Получаем случайное изображение из папки (чтобы потом сохранить его в переменной randomImage)
        File randomImage = getRandomImageFromFolder(directoryPath);

        // Если изображение найдено, продолжаем выполнение кода в этом блоке
        if (randomImage != null) {
            sendPostRequestWithImage(serverUrl, "", randomImage);  //вызов метода отправки запроса с изображением
        } else {
            System.out.println("Ничего в твоей папочке не нашлось(((");
        }
    }

    public static File getRandomImageFromFolder(String directoryPath) {
        File folder = new File(directoryPath);          //создание объекта file
        File[] files = folder.listFiles((dir, name) -> {      //вызов метода listFiles на объекте folder, которое фильтрует файлы в директории, ища только изображения с расширением .png, .jpg или .jpeg.
            String lowercaseName = name.toLowerCase();        //приводим файлы к нижнему регистру, чтобы сравнить расширения файлов
            return lowercaseName.endsWith(".png") || lowercaseName.endsWith(".jpg") || lowercaseName.endsWith(".jpeg") || lowercaseName.endsWith(".mov") || lowercaseName.endsWith(".mp4");    //Проверка, заканчивается ли имя файла на одно из указанных расширений. Если да, файл включается в финальный массив files.
        });

        if (files != null && files.length > 0) {    //проверка, что массив files не пустой и не null
            int randomFileIndex = new Random().nextInt(files.length);   //генерация случайного индекса для выбора файла из массива files
            return files[randomFileIndex];     //возврат случайного файла из массива files
        }
        return null;   //если условие не выполнено, возвращаем null
    }

    public static void sendPostRequestWithImage(String serverUrl, String textBody, File files) {
        HttpClient httpClient = HttpClients.createDefault();  //Создание нового экземпляра HttpClient

        // Создание массивов с различными ID для 'channel_id' и 'user_id'
        String[] channelIds = {"3ex6yj6pginmmk1b9rt94gw4fe", "iyxrxcupk3y5dxs3y9gzrj49bo", "i14kpdfrgjbg9jjtytnegnoxeh", "gp6ip5qngpdz3d86jzi9y1u5ec", "u8s6nqjrmpb9fku5oh7gai61ne", "fr8w6fpuhbfy5pbsx9dtsmsa6a", "ur7n68t7cpg3pkbj4i7s8gxwww", "pcsryboejfrpfj31fpuxrgx48y", "r8p97neaa38zz8dajstd39bzro", "ezybkpjbupyotrhruq93sfnjuo", "4n7igrrrctdodjzmh1oeutj3da", "7j1mpngcajgm7x876bjjow74qa", "9xzftak5b7g65d6aoqt91ojnne", "pkxr9upobfnd7rorajhih46yeo", "wc65b5bjnt8zbgy381jyjnf3bw"};

        String[] userIds = {"5cubkbo9hpbczc6n598ge9nawa", "wnfdd1kbmin47duiezqt6dewke", "kcy7zyz4eb8x3q3nrz7d7aidzh", "cymfc8wge3gozdog3a19xmnc4y", "d784n6k1htdyjd854wwtsn1g8c", "zr97oirpufdeuks113c8qk4kka", "azm6tyjhr3gniykg5rw9s9g4oh", "dy9de1xuiiyf5ynnnsziaj1ddy", "17bqhsn1p7n19eqk38am9cap3y", "fwb3g1x5gjrkjdqqfzkm466qko", "7yxzj1u9xfyqzcb4jnk1dryjxa", "hj637brabin8ppddtuamwjqq6c", "gd9gtxiazjrp3xj89gsw6wu3ky", "znd4dpa3mjryif8kkzyxe9wkxh", "ihz3prkdib8obc7fwc1jntfqcy"};

        // Отправляем запросы для каждого 'channel_id'
        for (int i = 0; i < channelIds.length; i++) { //

            // Создание нового объекта HttpPost
            HttpPost httpPost = new HttpPost(serverUrl);

            // Добавляем заголовки
            httpPost.addHeader("User-agent", "kittensBOT");
            httpPost.addHeader("Authorization", "Bearer mx48jefsqbdqxet7hqbn5cci6r");

            // Создание нового экземпляра MultipartEntityBuilder
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            //Выбор текущих значений ID канала и пользователя из соответствующих массивов.
            String currentChannelId = channelIds[i];
            String currentUserId = userIds[i];

            //Добавление в запрос текстового тела с идентификатором канала и двоичного тела с файлом изображения.
            builder.addTextBody("channel_id", currentChannelId, ContentType.TEXT_PLAIN);
            builder.addBinaryBody("files", files, ContentType.DEFAULT_BINARY, files.getName());

            //Построение многокомпонентного HTTP тела запроса.
            HttpEntity multipart = builder.build();

            //Установка созданного многокомпонентного HTTP тела в объект HttpPost
            httpPost.setEntity(multipart);

            System.out.println("мы дошли наконец до отправки картинки в хранилище");

            try {
                // Отправляем запрос и получаем ответ от сервера
                HttpResponse response = httpClient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();

                // Выводим информацию о статусе запроса
                System.out.println("HTTP Status Code for " + currentChannelId + " and " + currentUserId + ": " + statusCode);

                // Получение содержимого тела ответа
                HttpEntity responseEntity = response.getEntity();

                // Преобразование тела ответа в строку
                if (responseEntity != null) {
                    String responseBody = EntityUtils.toString(responseEntity);
                    System.out.println("Это ответ, посмотри: " + responseBody);

                    // Создание экземпляра ObjectMapper, который используется для работы с JSON-данными, т.е. для парсинга или генерации JSON.
                    ObjectMapper objectMapper = new ObjectMapper();

                    // Парсинг строкового ответа в JSON структуру
                    JsonNode responseJson = objectMapper.readTree(responseBody);

                    // Предполагая, что у нас есть массив file_infos из которого извлекается значение поля id первого объекта
                    String picturesId = responseJson.path("file_infos").get(0).path("id").asText();

                    // Выводим значение ID картинки
                    System.out.println("Значение ID картинки: " + picturesId);

                    ////////////////////////////////////////////////////////////

                    // После получения picturesId, отправляем второй POST запрос
                    //Объявление строки postServerUrl, которая содержит URL, на который будет отправлен второй POST запрос.
                    String postServerUrl = "https://time.tinkoff.ru/api/v4/posts";

                    // Создание пустого JSON объекта ObjectNode с помощью экземпляра ObjectMapper, используемого для манипуляции JSON данными.
                    ObjectNode jsonNode = objectMapper.createObjectNode();

                    //В JSON объект добавляется массив с именем "file_ids" и в этот массив добавляется ID изображения, полученного ранее.
                    jsonNode.putArray("file_ids").add(picturesId);

                    //В JSON объект добавляется строковое поле "message" с заданным текстом и тд
                    jsonNode.put("message", "Хорошего понедельничка тебе!) :kitty_for_dora:  :feet:");
                    jsonNode.put("user_id", currentUserId);
                    jsonNode.put("channel_id", currentChannelId);
                    jsonNode.put("create_at", 0);

                    // Создание нового экземпляра HttpClient, который будет использоваться для отправки второго POST запроса.
                    HttpClient httpClientForPost = HttpClients.createDefault();

                    //Инициализация объекта HttpPost с URL для отправки POST запроса.
                    HttpPost postRequest = new HttpPost(postServerUrl);

                    //Добавление заголовков
                    //"Content-Type" с типом MIME (application/json), указывающего что тело запроса будет в формате JSON.
                    postRequest.addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());

                    //Создание StringEntity, который содержит JSON данные в виде строки с установленным типом контента APPLICATION_JSON.
                    StringEntity entity = new StringEntity(objectMapper.writeValueAsString(jsonNode), ContentType.APPLICATION_JSON);

                    //Установка JSON данных как HTTP для тела запроса.
                    postRequest.setEntity(entity);

                    //Добавление различных HTTP-заголовков
                    postRequest.addHeader("Authorization", "Bearer mx48jefsqbdqxet7hqbn5cci6r");
                    postRequest.addHeader("User-agent", "kittensBOT");
                    postRequest.addHeader("x-requested-with", "XMLHttpRequest");
                    postRequest.addHeader("Content-Type", "application/json");

                    // Отправка 2 POST запроса и получение ответа
                    HttpResponse postResponse = httpClientForPost.execute(postRequest);

                    // Извлечение тела ответа из HTTP ответа.
                    HttpEntity responseEntityPost = postResponse.getEntity();

                    // Преобразование тела ответа в строку и выводим инфо о результате запроса в консоль
                    if (responseEntityPost != null) {
                        String responseBody2 = EntityUtils.toString(responseEntityPost);
                        System.out.printf("Ответ на второй POST запрос для channel_id '%s' и user_id '%s': %s%n", currentChannelId, currentUserId, responseBody2);
                    }
                    EntityUtils.consume(responseEntity); // Освобождаем ресурсы, закрывая соединение сущности
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Освобождаем ресурсы, связанные с HttpPost
                httpPost.releaseConnection();
            }
        }
    }
}
