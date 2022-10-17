package repository;

import domain.LocationDate;
import domain.WifiInfo;
import logger.LoggingController;
import util.SqlUtil;
import util.Util;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class DaoManager {
    public int insertWifiInfos(List<WifiInfo> wifiInfoList) {
        try (
                Connection connection = ConnManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SqlUtil.INSERT_WIFI_INFO_SQL)) {
            for (WifiInfo wifiInfo : wifiInfoList) {
                int idx = 0;
                preparedStatement.setString(++idx, wifiInfo.getAdminNumber());
                preparedStatement.setString(++idx, wifiInfo.getBorough());
                preparedStatement.setString(++idx, wifiInfo.getWifiName());
                preparedStatement.setString(++idx, wifiInfo.getLoadName());
                preparedStatement.setString(++idx, wifiInfo.getDetailAddress());
                preparedStatement.setString(++idx, wifiInfo.getInstallPosition());
                preparedStatement.setString(++idx, wifiInfo.getInstallType());
                preparedStatement.setString(++idx, wifiInfo.getInstallAgency());
                preparedStatement.setString(++idx, wifiInfo.getServiceType());
                preparedStatement.setString(++idx, wifiInfo.getNetType());
                preparedStatement.setInt(++idx, wifiInfo.getInstallYear());
                preparedStatement.setString(++idx, wifiInfo.getInOutDoorType());
                preparedStatement.setString(++idx, wifiInfo.getWifiConnEnv());
                preparedStatement.setDouble(++idx, wifiInfo.getLocationDate().getPosX());
                preparedStatement.setDouble(++idx, wifiInfo.getLocationDate().getPosY());
                preparedStatement.setString(++idx, wifiInfo.getLocationDate().getDateTime());

                preparedStatement.addBatch();
                preparedStatement.clearParameters();
            }
            preparedStatement.executeBatch();
        } catch (Exception e) {
            LoggingController.log(Level.INFO, "DaoManager:insertWifiInfos error occur => " + e);
            return 0;
        }
        return wifiInfoList.size();
    }

    public int deleteWifiInfo() {
        try (
                Connection connection = ConnManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SqlUtil.DELETE_WIFI_INFO_SQL)) {
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            LoggingController.log(Level.INFO, "DaoManager:deleteWifiInfo error occur => " + e);
            return 0;
        }
    }

    public boolean checkWifiInfoExist() {
        try (
                Connection connection = ConnManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SqlUtil.SELECT_COUNT_WIFI_INFO_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int cnt = resultSet.getInt("COUNT");
                return cnt > 0;
            }
        } catch (Exception e) {
            LoggingController.log(Level.INFO, "DaoManager:checkWifiInfoExist error occur => " + e);
        }
        return false;
    }

    public Optional<List<WifiInfo>> searchNearestWifiInfo(double posX, double posY, int offset, int cnt) {
        try (
                Connection connection = ConnManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SqlUtil.SELECT_PAGE_WIFI_INFO_SQL)) {
            int idx = 0;

            preparedStatement.setDouble(++idx, posX);
            preparedStatement.setDouble(++idx, posY);
            preparedStatement.setDouble(++idx, posX);
            preparedStatement.setDouble(++idx, posX);
            preparedStatement.setDouble(++idx, posY);
            preparedStatement.setDouble(++idx, posX);
            preparedStatement.setInt(++idx, offset);
            preparedStatement.setInt(++idx, cnt);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<WifiInfo> wifiInfoList = new ArrayList<>();

            while (resultSet.next()) {
                WifiInfo wifiInfo = WifiInfo.builder()
                        .distance(resultSet.getDouble(WifiInfo.WifiEnum.DISTANCE))
                        .adminNumber(resultSet.getString(WifiInfo.WifiEnum.ADMIN_NUMBER))
                        .borough(resultSet.getString(WifiInfo.WifiEnum.BOROUGH))
                        .wifiName(resultSet.getString(WifiInfo.WifiEnum.WIFI_NAME))
                        .loadName(resultSet.getString(WifiInfo.WifiEnum.LOAD_NAME))
                        .detailAddress(resultSet.getString(WifiInfo.WifiEnum.DETAIL_ADDRESS))
                        .installPosition(resultSet.getString(WifiInfo.WifiEnum.INSTALL_POSITION))
                        .installType(resultSet.getString(WifiInfo.WifiEnum.INSTALL_TYPE))
                        .installAgency(resultSet.getString(WifiInfo.WifiEnum.INSTALL_AGENCY))
                        .serviceType(resultSet.getString(WifiInfo.WifiEnum.SERVICE_TYPE))
                        .netType(resultSet.getString(WifiInfo.WifiEnum.NET_TYPE))
                        .installYear(resultSet.getInt(WifiInfo.WifiEnum.INSTALL_YEAR))
                        .inOutDoorType(resultSet.getString(WifiInfo.WifiEnum.INOUT_DOOR_TYPE))
                        .wifiConnEnv(resultSet.getString(WifiInfo.WifiEnum.WIFI_CONN_ENV))
                        .locationDate(LocationDate.builder()
                                .posX(resultSet.getDouble(WifiInfo.WifiEnum.POS_X))
                                .posY(resultSet.getDouble(WifiInfo.WifiEnum.POS_Y))
                                .dateTime(resultSet.getString(WifiInfo.WifiEnum.DATE_TIME))
                                .build())
                        .build();
                wifiInfoList.add(wifiInfo);
            }
            return Optional.ofNullable(wifiInfoList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<List<LocationDate>> searchLocationHistoryInfos() {
        try (
                Connection connection = ConnManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SqlUtil.SELECT_LOCATION_HISTORY_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<LocationDate> locationDateList = new ArrayList<>();

            while (resultSet.next()) {
                LocationDate locationDate = LocationDate.builder()
                        .id(resultSet.getInt(LocationDate.LocationDateEnum.ID))
                        .posX(resultSet.getDouble(LocationDate.LocationDateEnum.POS_X))
                        .posY(resultSet.getDouble(LocationDate.LocationDateEnum.POS_Y))
                        .dateTime(resultSet.getString(LocationDate.LocationDateEnum.DATETIME))
                        .build();
                locationDateList.add(locationDate);
            }
            return Optional.ofNullable(locationDateList);
        } catch (Exception e) {
            LoggingController.log(Level.INFO, "DaoManager:searchLocationHistoryInfos error occur => " + e);
            return Optional.empty();
        }
    }

    public int insertLocationInfo(double posX, double posY) {
        try (
                Connection connection = ConnManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SqlUtil.INSERT_LOCATION_HISTORY_SQL)) {
            int idx = 0;
            preparedStatement.setDouble(++idx, posX);
            preparedStatement.setDouble(++idx, posY);
            preparedStatement.setString(++idx, Util.formatLocationHistoryDateTimeStr(LocalDateTime.now()));
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            LoggingController.log(Level.INFO, "DaoManager:insertLocationInfo error occur => " + e);
            return 0;
        }
    }

    public int deleteLocationInfo(int id) {
        try (
                Connection connection = ConnManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SqlUtil.DELETE_LOCATION_HISTORY_SQL)) {
            int idx = 0;
            preparedStatement.setLong(++idx, id);
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            LoggingController.log(Level.INFO, "DaoManager:deleteLocationInfo error occur => " + e);
            return 0;
        }
    }
}
