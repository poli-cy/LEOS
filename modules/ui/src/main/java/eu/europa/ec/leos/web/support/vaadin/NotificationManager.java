/*
 * Copyright 2019 European Commission
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.leos.web.support.vaadin;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import eu.europa.ec.leos.web.event.NotificationEvent;
import eu.europa.ec.leos.i18n.MessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@UIScope
@Component
public class NotificationManager {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationManager.class);

    private static final String NOTIFICATION_STYLE = "dark";
    private static final int TRAY_MESSAGE_NOTIFICATION_DELAY = 5000;
    private static final int MESSAGE_NOTIFICATION_DELAY = 3000;

    private final MessageHelper messageHelper;
    private final EventBus eventBus;

    @Autowired
    public NotificationManager(MessageHelper messageHelper, EventBus eventBus) {
        this.messageHelper = messageHelper;
        this.eventBus = eventBus;
    }

    @PostConstruct
    private void init(){
        eventBus.register(this);
    }

    @Subscribe
    public void showNotification(final NotificationEvent notificationEvent) {
        // NOTE notification exceptions should not be propagated. We don't know how to handle them somewhere else.
        try {
            UI currentUI = UI.getCurrent();
            if(notificationEvent.getTargetedUI() != null) {
                currentUI =  notificationEvent.getTargetedUI();
            } else if (currentUI != null) {
                notificationEvent.setTargetedUI(currentUI);
            }
            if(currentUI != null){
                currentUI.access(() -> {
                    switch (notificationEvent.getType()) {
                        case TRAY:
                            handleTrayNotification(notificationEvent);
                            break;
                        case INFO:
                            handleNotification(notificationEvent, Type.HUMANIZED_MESSAGE);
                            break;
                        case WARNING:
                            handleNotification(notificationEvent, Type.WARNING_MESSAGE);
                            break;
                        case ERROR:
                            handleErrorNotification(notificationEvent);
                            break;
                        default:
                            handleNotification(notificationEvent, Type.ERROR_MESSAGE);
                            break;
                    }
                });
            }
        } catch (Exception ex) {
            LOG.error("Unable to handle notification event!", ex);
        }
    }

    private void handleTrayNotification(NotificationEvent notificationEvent) {
        String message = messageHelper.getMessage(notificationEvent.getMessageKey(), notificationEvent.getArgs());
        String caption = messageHelper.getMessage(notificationEvent.getCaptionKey());
        Notification notification = new Notification(caption, message, Type.TRAY_NOTIFICATION, true);
        notification.setStyleName(NOTIFICATION_STYLE);
        notification.setDelayMsec(TRAY_MESSAGE_NOTIFICATION_DELAY);
        notification.show(notificationEvent.getTargetedUI() != null ? notificationEvent.getTargetedUI().getPage() : Page.getCurrent());
    }

    private void handleNotification(NotificationEvent notificationEvent, Notification.Type notificationType) {
        String message = messageHelper.getMessage(notificationEvent.getMessageKey(), notificationEvent.getArgs());
        Notification notification = new Notification(message, null, notificationType, true);
        notification.setStyleName(NOTIFICATION_STYLE);
        notification.setDelayMsec(MESSAGE_NOTIFICATION_DELAY);
        notification.show(notificationEvent.getTargetedUI() != null ? notificationEvent.getTargetedUI().getPage() : Page.getCurrent());
    }

    private void handleErrorNotification(NotificationEvent notificationEvent) {
        String message = messageHelper.getMessage(notificationEvent.getMessageKey(), notificationEvent.getArgs());
        Notification notification = new Notification(message, null, Type.ERROR_MESSAGE, true);
        notification.show(notificationEvent.getTargetedUI() != null ? notificationEvent.getTargetedUI().getPage() : Page.getCurrent());
    }
}
