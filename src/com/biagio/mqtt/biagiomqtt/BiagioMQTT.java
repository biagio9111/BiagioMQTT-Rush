
package com.biagio.mqtt.biagiomqtt;

import com.google.appinventor.components.annotations.Extension;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.UsesPermissions;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;

import android.os.Handler;
import android.os.Looper;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

@Extension(
    description = "Controllo MQTT per Wokwi e Kodular",
    icon = "icon.png"
)
@UsesPermissions(
    permissionNames = "android.permission.INTERNET,android.permission.ACCESS_NETWORK_STATE"
)
public class BiagioMQTT extends AndroidNonvisibleComponent {

    private MqttAsyncClient client;
    private final Handler ui = new Handler(Looper.getMainLooper());
    private String savedTopic = "";
    private boolean connecting = false;

    public BiagioMQTT(ComponentContainer container) {
        super(container.$form());
    }

    private void error(String operation, Throwable e) {
        String message = e == null ? "Errore sconosciuto" : String.valueOf(e.getMessage());
        ui.post(() -> ErrorOccurred(operation, message));
    }

    @SimpleFunction
    public void Connect(String host, int port, String clientId) {
        if (connecting) return;
        if (IsConnected()) {
            Connected();
            return;
        }

        if (host == null || host.trim().isEmpty()
                || port < 1 || port > 65535
                || clientId == null || clientId.trim().isEmpty()) {
            ErrorOccurred("Connect", "Parametri non validi");
            return;
        }

        connecting = true;

        try {
            if (client != null) {
                try { client.close(); } catch (Exception ignored) {}
            }

            final MqttAsyncClient mqtt = new MqttAsyncClient(
                "tcp://" + host.trim() + ":" + port,
                clientId.trim(),
                new MemoryPersistence()
            );
            client = mqtt;

            mqtt.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String uri) {
                    connecting = false;
                    if (client != mqtt) return;
                    ui.post(() -> Connected());
                    if (!savedTopic.isEmpty()) {
                        subscribeInternal(mqtt, savedTopic);
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    if (client != mqtt) return;
                    ui.post(() -> Disconnected(
                        cause == null ? "Connessione persa" :
                        String.valueOf(cause.getMessage())
                    ));
                }

                @Override
                public void messageArrived(String topic, MqttMessage msg) {
                    String value = new String(
                        msg.getPayload(),
                        java.nio.charset.StandardCharsets.UTF_8
                    );
                    ui.post(() -> MessageReceived(topic, value));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {}
            });

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(30);

            mqtt.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken token) {
                    connecting = false;
                }

                @Override
                public void onFailure(IMqttToken token, Throwable e) {
                    connecting = false;
                    error("Connect", e);
                }
            });

        } catch (Exception e) {
            connecting = false;
            error("Connect", e);
        }
    }

    @SimpleFunction
    public void Publish(String topic, String payload) {
        try {
            if (!IsConnected()) {
                ErrorOccurred("Publish", "MQTT non connesso");
                return;
            }

            client.publish(
                topic,
                payload.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                0,
                false,
                null,
                new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken token) {
                        ui.post(() -> Published(topic));
                    }

                    @Override
                    public void onFailure(IMqttToken token, Throwable e) {
                        error("Publish", e);
                    }
                }
            );
        } catch (Exception e) {
            error("Publish", e);
        }
    }

    private void subscribeInternal(MqttAsyncClient mqtt, String topic) {
        try {
            mqtt.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken token) {
                    ui.post(() -> Subscribed(topic));
                }

                @Override
                public void onFailure(IMqttToken token, Throwable e) {
                    error("Subscribe", e);
                }
            });
        } catch (Exception e) {
            error("Subscribe", e);
        }
    }

    @SimpleFunction
    public void Subscribe(String topic) {
        if (topic == null || topic.isEmpty()) {
            ErrorOccurred("Subscribe", "Topic vuoto");
            return;
        }
        savedTopic = topic;
        if (IsConnected()) {
            subscribeInternal(client, topic);
        }
    }

    @SimpleFunction
    public boolean IsConnected() {
        return client != null && client.isConnected();
    }

    @SimpleFunction
    public void Disconnect() {
        try {
            savedTopic = "";
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        } catch (Exception e) {
            error("Disconnect", e);
        }
    }

    @SimpleEvent
    public void Connected() {
        EventDispatcher.dispatchEvent(this, "Connected");
    }

    @SimpleEvent
    public void Disconnected(String reason) {
        EventDispatcher.dispatchEvent(this, "Disconnected", reason);
    }

    @SimpleEvent
    public void MessageReceived(String topic, String payload) {
        EventDispatcher.dispatchEvent(this, "MessageReceived", topic, payload);
    }

    @SimpleEvent
    public void Published(String topic) {
        EventDispatcher.dispatchEvent(this, "Published", topic);
    }

    @SimpleEvent
    public void Subscribed(String topic) {
        EventDispatcher.dispatchEvent(this, "Subscribed", topic);
    }

    @SimpleEvent
    public void ErrorOccurred(String operation, String message) {
        EventDispatcher.dispatchEvent(this, "ErrorOccurred", operation, message);
    }
}
