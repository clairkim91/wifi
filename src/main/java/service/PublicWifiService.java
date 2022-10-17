package service;

import com.google.gson.JsonObject;
import domain.WifiInfo;
import logger.LoggingController;
import repository.ConnManager;
import repository.DaoManager;
import util.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PublicWifiService {
    private final DaoManager daoManager = new DaoManager();

    public int requestWifiApiService() {
        try {
            Future<Integer> search = CompletableFuture.supplyAsync(() -> {
                List<CompletableFuture<Integer>> completableFutures = new ArrayList<>();
                int searchCount = 0;

                for (int i = 0, start = 1, end = 1000; i < ConfigUtil.getWorkConfig().getWorkCount(); i++, start += 1000, end += 1000) {
                    final int _start = start;
                    final int _end = end;

                    completableFutures.add(
                            CompletableFuture.supplyAsync(() -> {
                                        // Wifi 서비스 HTTP 요청 및 파싱
                                        try {
                                            String jsonStr = HttpUtil.sendHttpRequest(_start, _end);
                                            JsonObject jsonObject = JsonUtil.parseStrToJsonObject(jsonStr);
                                            return jsonObject;
                                        } catch (Exception e) {
                                            LoggingController.log(Level.INFO,"PublicWifiService:requestWifiApiService error occur => " + e);
                                            return null;
                                        }
                                    }, ThreadUtil.getThreadPoolExecutor()).thenApply(JsonUtil::parseJsonToWifiInfo)
                                    .thenCompose(future -> CompletableFuture.supplyAsync(() -> {
                                        // DB에 해당 List(WifiInfo) 정보 저장, 저장 횟수 반환
                                        if (future.isPresent()) {
                                            // TODO
                                            return daoManager.insertWifiInfos(future.get());
                                        }
                                        return 0;
                                    }, ThreadUtil.getThreadPoolExecutor())));
                }
                searchCount = completableFutures.stream().map(CompletableFuture::join).mapToInt(i -> i).sum();
                return searchCount;
            });
            return search.get(ConfigUtil.getWorkConfig().getTimeUnit(), TimeUnit.SECONDS);
        } catch (Exception e) {
            LoggingController.log(Level.INFO, "PublicWifiService:requestWifiApiService error occur => " + e);
            return 0;
        }
    }

    public List<WifiInfo> searchWifiNearestInfosByDB(double posX, double posY, int offSet, int cnt) {
        try {
            Optional<List<WifiInfo>> nearestWifiInfos = daoManager.searchNearestWifiInfo(posX, posY, offSet, cnt);

            if (nearestWifiInfos.isPresent()) {
                return nearestWifiInfos.get();
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            LoggingController.log(Level.INFO,"PublicWifiService:searchWifiNearestInfosByDB error occur => " + e);
            return new ArrayList<>();
        }
    }

    public List<WifiInfo> searchWifiNearestInfosDirectly(double posX, double posY, int offSet, int cnt) {
        try {
            ConcurrentLinkedQueue<WifiInfo> wifiInfoQueue = new ConcurrentLinkedQueue<WifiInfo>();

            Future<Integer> search = CompletableFuture.supplyAsync(() -> {
                List<CompletableFuture<Integer>> completableFutures = new ArrayList<>();
                int searchCount = 0;

                for (int i = 0, start = 1, end = 1000; i < ConfigUtil.getWorkConfig().getWorkCount(); i++, start += 1000, end += 1000) {
                    final int _start = start;
                    final int _end = end;

                    completableFutures.add(
                            CompletableFuture.supplyAsync(() -> {
                                        // Wifi 서비스 HTTP 요청 및 파싱
                                        try {
                                            String jsonStr = HttpUtil.sendHttpRequest(_start, _end);
                                            JsonObject jsonObject = JsonUtil.parseStrToJsonObject(jsonStr);
                                            return jsonObject;
                                        } catch (Exception e) {
                                            LoggingController.log(Level.INFO,"PublicWifiService:searchWifiNearestInfosDirectly error occur => " + e);
                                            return null;
                                        }
                                    }, ThreadUtil.getThreadPoolExecutor()).thenApply(JsonUtil::parseJsonToWifiInfo)
                                    .thenCompose(future -> CompletableFuture.supplyAsync(() -> {
                                        // list에 해당 List(WifiInfo) 정보 저장, 저장 횟수 반환
                                        if (future.isPresent()) {
                                            // TODO
                                            wifiInfoQueue.addAll(future.get());
                                            return future.get().size();
                                        }
                                        return 0;
                                    }, ThreadUtil.getThreadPoolExecutor())));
                }
                searchCount = completableFutures.stream().map(CompletableFuture::join).mapToInt(i -> i).sum();
                return searchCount;
            });
            search.get(ConfigUtil.getWorkConfig().getTimeUnit(), TimeUnit.SECONDS);

            return wifiInfoQueue.stream().sorted((a, b) -> {
                        double distanceA = Util.calculateDistance(posX, posY, a.getLocationDate().getPosX(), a.getLocationDate().getPosY());
                        double distanceB = Util.calculateDistance(posX, posY, b.getLocationDate().getPosX(), b.getLocationDate().getPosY());
                        return Double.compare(distanceA, distanceB);
                    }).limit(cnt).map(wifiInfo -> {
                        double distance = Util.calculateDistance(posX, posY, wifiInfo.getLocationDate().getPosX(), wifiInfo.getLocationDate().getPosY());
                        double rDistance = Math.round((distance / 1000) * 10000) / 10000.0;
                        wifiInfo.setDistance(rDistance);
                        return wifiInfo;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LoggingController.log(Level.INFO,"PublicWifiService:searchWifiNearestInfosDirectly error occur => " + e);
            return new ArrayList<>();
        }
    }

    public boolean checkWifiInfoExist() {
        return daoManager.checkWifiInfoExist();
    }

    public boolean checkDBConnection() {
        try {
            return ConnManager.getConnection().isClosed() ? false : true;
        } catch (Exception e) {
            LoggingController.log(Level.INFO,"PublicWifiService:checkDBConnection error occur => " + e);
            return false;
        }
    }

    public int deleteWifiInfo() {
        return daoManager.deleteWifiInfo();
    }
}
