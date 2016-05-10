package com.mikepenz.fastadapter.app.dummy;

import com.mikepenz.fastadapter.app.items.ImageItem;
import com.mikepenz.fastadapter.app.items.SimpleImageItem;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by mikepenz on 08.01.16.
 */
public class ImageDummyData {

    public static List<ImageItem> getImageItems() {
        return toList(
                new ImageItem().withIdentifier(1L).withName("Yang Zhuo Yong Cuo, Tibet China").withDescription("#100063").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/yang_zhuo_yong_cuo,_tibet-china-63.jpg"),
                new ImageItem().withIdentifier(2L).withName("Yellowstone United States").withDescription("#100017").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/yellowstone-united_states-17.jpg"),
                new ImageItem().withIdentifier(3L).withName("Victoria Australia").withDescription("#100031").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/victoria-australia-31.jpg"),
                new ImageItem().withIdentifier(4L).withName("Valencia Spain").withDescription("#100082").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/valencia-spain-82.jpg"),
                new ImageItem().withIdentifier(5L).withName("Xigaze, Tibet China").withDescription("#100030").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/xigaze,_tibet-china-30.jpg"),
                new ImageItem().withIdentifier(6L).withName("Utah United States").withDescription("#100096").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/utah-united_states-96.jpg"),
                new ImageItem().withIdentifier(7L).withName("Utah United States").withDescription("#100015").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/utah-united_states-15.jpg"),
                new ImageItem().withIdentifier(8L).withName("Utah United States").withDescription("#100088").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/utah-united_states-88.jpg"),
                new ImageItem().withIdentifier(9L).withName("Umm Al Quwain United Arab Emirates").withDescription("#100013").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/umm_al_quwain-united_arab_emirates-13.jpg"),
                new ImageItem().withIdentifier(10L).withName("Texas United States").withDescription("#100026").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/texas-united_states-26.jpg"),
                new ImageItem().withIdentifier(11L).withName("Siuslaw National Forest United States").withDescription("#100092").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/siuslaw_national_forest-united_states-92.jpg"),
                new ImageItem().withIdentifier(12L).withName("The Minquiers Channel Islands").withDescription("#100069").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/the_minquiers-channel_islands-69.jpg"),
                new ImageItem().withIdentifier(13L).withName("Texas United States").withDescription("#100084").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/texas-united_states-84.jpg"),
                new ImageItem().withIdentifier(14L).withName("Tabuaeran Kiribati").withDescription("#100050").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/tabuaeran-kiribati-50.jpg"),
                new ImageItem().withIdentifier(15L).withName("Stanislaus River United States").withDescription("#100061").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/stanislaus_river-united_states-61.jpg"),
                new ImageItem().withIdentifier(16L).withName("S?ehitkamil Turkey").withDescription("#100072").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/s?ehitkamil-turkey-72.jpg"),
                new ImageItem().withIdentifier(17L).withName("Salinas Grandes Argentina").withDescription("#100025").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/salinas_grandes-argentina-25.jpg"),
                new ImageItem().withIdentifier(18L).withName("Shadegan Refuge Iran").withDescription("#100012").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/shadegan_refuge-iran-12.jpg"),
                new ImageItem().withIdentifier(19L).withName("San Pedro De Atacama Chile").withDescription("#100043").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/san_pedro_de_atacama-chile-43.jpg"),
                new ImageItem().withIdentifier(20L).withName("Ragged Island The Bahamas").withDescription("#100064").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/ragged_island-the_bahamas-64.jpg"),
                new ImageItem().withIdentifier(21L).withName("Qinghai Lake China").withDescription("#100080").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/qinghai_lake-china-80.jpg"),
                new ImageItem().withIdentifier(22L).withName("Qesm Al Wahat Ad Dakhlah Egypt").withDescription("#100056").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/qesm_al_wahat_ad_dakhlah-egypt-56.jpg"),
                new ImageItem().withIdentifier(23L).withName("Riedstadt Germany").withDescription("#100042").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/riedstadt-germany-42.jpg"),
                new ImageItem().withIdentifier(24L).withName("Redwood City United States").withDescription("#100048").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/redwood_city-united_states-48.jpg"),
                new ImageItem().withIdentifier(25L).withName("Nyingchi, Tibet China").withDescription("#100098").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/nyingchi,_tibet-china-98.jpg"),
                new ImageItem().withIdentifier(26L).withName("Ngari, Tibet China").withDescription("#100057").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/ngari,_tibet-china-57.jpg"),
                new ImageItem().withIdentifier(27L).withName("Pozoantiguo Spain").withDescription("#100099").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/pozoantiguo-spain-99.jpg"),
                new ImageItem().withIdentifier(28L).withName("Ningaloo Australia").withDescription("#100073").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/ningaloo-australia-73.jpg"),
                new ImageItem().withIdentifier(29L).withName("Niederzier Germany").withDescription("#100079").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/niederzier-germany-79.jpg"),
                new ImageItem().withIdentifier(30L).withName("Olympic Dam Australia").withDescription("#100065").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/olympic_dam-australia-65.jpg"),
                new ImageItem().withIdentifier(31L).withName("Peedamulla Australia").withDescription("#100040").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/peedamulla-australia-40.jpg"),
                new ImageItem().withIdentifier(32L).withName("Nevado Tres Cruces Park Chile").withDescription("#100089").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/nevado_tres_cruces_park-chile-89.jpg")
        );
    }

    public static SimpleImageItem getDummyItem() {
        int ran = new Random().nextInt(3);
        if (ran == 0) {
            return new SimpleImageItem().withName("NEW").withDescription("Newly added item").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/yang_zhuo_yong_cuo,_tibet-china-63.jpg");
        } else if (ran == 1) {
            return new SimpleImageItem().withName("NEW").withDescription("Newly added item").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/yellowstone-united_states-17.jpg");
        } else {
            return new SimpleImageItem().withName("NEW").withDescription("Newly added item").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/victoria-australia-31.jpg");
        }
    }

    public static List<SimpleImageItem> getSimpleImageItems() {
        return toList(
                new SimpleImageItem().withIdentifier(1L).withName("Yang Zhuo Yong Cuo, Tibet China").withDescription("#100063").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/yang_zhuo_yong_cuo,_tibet-china-63.jpg"),
                new SimpleImageItem().withIdentifier(2L).withName("Yellowstone United States").withDescription("#100017").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/yellowstone-united_states-17.jpg"),
                new SimpleImageItem().withIdentifier(3L).withName("Victoria Australia").withDescription("#100031").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/victoria-australia-31.jpg"),
                new SimpleImageItem().withIdentifier(4L).withName("Valencia Spain").withDescription("#100082").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/valencia-spain-82.jpg"),
                new SimpleImageItem().withIdentifier(5L).withName("Xigaze, Tibet China").withDescription("#100030").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/xigaze,_tibet-china-30.jpg"),
                new SimpleImageItem().withIdentifier(6L).withName("Utah United States").withDescription("#100096").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/utah-united_states-96.jpg"),
                new SimpleImageItem().withIdentifier(7L).withName("Utah United States").withDescription("#100015").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/utah-united_states-15.jpg"),
                new SimpleImageItem().withIdentifier(8L).withName("Utah United States").withDescription("#100088").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/utah-united_states-88.jpg"),
                new SimpleImageItem().withIdentifier(9L).withName("Umm Al Quwain United Arab Emirates").withDescription("#100013").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/umm_al_quwain-united_arab_emirates-13.jpg"),
                new SimpleImageItem().withIdentifier(10L).withName("Texas United States").withDescription("#100026").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/texas-united_states-26.jpg"),
                new SimpleImageItem().withIdentifier(11L).withName("Siuslaw National Forest United States").withDescription("#100092").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/siuslaw_national_forest-united_states-92.jpg"),
                new SimpleImageItem().withIdentifier(12L).withName("The Minquiers Channel Islands").withDescription("#100069").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/the_minquiers-channel_islands-69.jpg"),
                new SimpleImageItem().withIdentifier(13L).withName("Texas United States").withDescription("#100084").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/texas-united_states-84.jpg"),
                new SimpleImageItem().withIdentifier(14L).withName("Tabuaeran Kiribati").withDescription("#100050").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/tabuaeran-kiribati-50.jpg"),
                new SimpleImageItem().withIdentifier(15L).withName("Stanislaus River United States").withDescription("#100061").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/stanislaus_river-united_states-61.jpg"),
                new SimpleImageItem().withIdentifier(16L).withName("Salinas Grandes Argentina").withDescription("#100025").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/salinas_grandes-argentina-25.jpg"),
                new SimpleImageItem().withIdentifier(17L).withName("Shadegan Refuge Iran").withDescription("#100012").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/shadegan_refuge-iran-12.jpg"),
                new SimpleImageItem().withIdentifier(18L).withName("San Pedro De Atacama Chile").withDescription("#100043").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/san_pedro_de_atacama-chile-43.jpg"),
                new SimpleImageItem().withIdentifier(19L).withName("Ragged Island The Bahamas").withDescription("#100064").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/ragged_island-the_bahamas-64.jpg"),
                new SimpleImageItem().withIdentifier(20L).withName("Qinghai Lake China").withDescription("#100080").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/qinghai_lake-china-80.jpg"),
                new SimpleImageItem().withIdentifier(21L).withName("Qesm Al Wahat Ad Dakhlah Egypt").withDescription("#100056").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/qesm_al_wahat_ad_dakhlah-egypt-56.jpg"),
                new SimpleImageItem().withIdentifier(22L).withName("Riedstadt Germany").withDescription("#100042").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/riedstadt-germany-42.jpg"),
                new SimpleImageItem().withIdentifier(23L).withName("Redwood City United States").withDescription("#100048").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/redwood_city-united_states-48.jpg"),
                new SimpleImageItem().withIdentifier(24L).withName("Nyingchi, Tibet China").withDescription("#100098").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/nyingchi,_tibet-china-98.jpg"),
                new SimpleImageItem().withIdentifier(25L).withName("Ngari, Tibet China").withDescription("#100057").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/ngari,_tibet-china-57.jpg"),
                new SimpleImageItem().withIdentifier(26L).withName("Pozoantiguo Spain").withDescription("#100099").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/pozoantiguo-spain-99.jpg"),
                new SimpleImageItem().withIdentifier(27L).withName("Ningaloo Australia").withDescription("#100073").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/ningaloo-australia-73.jpg"),
                new SimpleImageItem().withIdentifier(28L).withName("Niederzier Germany").withDescription("#100079").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/niederzier-germany-79.jpg"),
                new SimpleImageItem().withIdentifier(29L).withName("Olympic Dam Australia").withDescription("#100065").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/olympic_dam-australia-65.jpg"),
                new SimpleImageItem().withIdentifier(30L).withName("Peedamulla Australia").withDescription("#100040").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/peedamulla-australia-40.jpg"),
                new SimpleImageItem().withIdentifier(31L).withName("Nevado Tres Cruces Park Chile").withDescription("#100089").withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/nevado_tres_cruces_park-chile-89.jpg")
        );
    }

    private static List<ImageItem> toList(ImageItem... imageItems) {
        return Arrays.asList(imageItems);
    }

    private static List<SimpleImageItem> toList(SimpleImageItem... imageItems) {
        return Arrays.asList(imageItems);
    }
}
