import { useState, useCallback } from 'react';

export type NotificationType = 'success' | 'error' | 'warning' | 'info';

export interface Notification {
  id: string;
  type: NotificationType;
  message: string;
  duration?: number;
}

export const useNotification = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);

  const addNotification = useCallback((notification: Omit<Notification, 'id'>) => {
    const id = Math.random().toString(36).substr(2, 9);
    const newNotification: Notification = {
      ...notification,
      id,
      duration: notification.duration || 5000,
    };

    setNotifications(prev => [...prev, newNotification]);

    // Auto remove notification
    setTimeout(() => {
      setNotifications(prev => prev.filter(n => n.id !== id));
    }, newNotification.duration);

    return id;
  }, []);

  const removeNotification = useCallback((id: string) => {
    setNotifications(prev => prev.filter(n => n.id !== id));
  }, []);

  const showSuccess = useCallback((message: string, duration?: number) => {
    return addNotification({ type: 'success', message, duration });
  }, [addNotification]);

  const showError = useCallback((message: string, duration?: number) => {
    return addNotification({ type: 'error', message, duration });
  }, [addNotification]);

  const showWarning = useCallback((message: string, duration?: number) => {
    return addNotification({ type: 'warning', message, duration });
  }, [addNotification]);

  const showInfo = useCallback((message: string, duration?: number) => {
    return addNotification({ type: 'info', message, duration });
  }, [addNotification]);

  return {
    notifications,
    addNotification,
    removeNotification,
    showSuccess,
    showError,
    showWarning,
    showInfo,
  };
}; 