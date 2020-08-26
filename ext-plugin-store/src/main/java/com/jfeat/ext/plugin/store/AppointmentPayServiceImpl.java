package com.jfeat.ext.plugin.store;

import com.jfeat.ext.plugin.BasePlugin;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.StorePlugin;
import com.jfeat.ext.plugin.store.bean.Appointment;
import com.jfeat.service.exception.RetrieveOrderException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/8/27
 */
public class AppointmentPayServiceImpl implements AppointmentPayService {

    @Override
    public Map<String, Object> retrieveToPayOrder(String orderNumber) throws RetrieveOrderException {
        BasePlugin storePlugin = ExtPluginHolder.me().get(StorePlugin.class);
        if (!storePlugin.isEnabled()) {
            throw new RetrieveOrderException("store plugin is not enabled.");
        }
        Appointment appointment;
        try {
            AppointmentApi appointmentApi = new AppointmentApi();
            appointment = appointmentApi.getAppointment(orderNumber);
        }
        catch (Exception ex) {
            throw new RetrieveOrderException(ex.getMessage());
        }
        Map<String, Object> res = new HashMap<>();
        res.put("description", appointment.getItemName());
        res.put("total_price", appointment.getFee());
        return res;
    }

    @Override
    public void paidNotify(String orderNumber, String paymentType, String tradeNumber, String payAccount) throws RetrieveOrderException {
        BasePlugin storePlugin = ExtPluginHolder.me().get(StorePlugin.class);
        if (!storePlugin.isEnabled()) {
            throw new RetrieveOrderException("store plugin is not enabled.");
        }

        AppointmentApi appointmentApi = new AppointmentApi();
        if (!appointmentApi.payNotify(orderNumber, paymentType, tradeNumber)) {
            throw new RetrieveOrderException("pay error.");
        }
    }
}
