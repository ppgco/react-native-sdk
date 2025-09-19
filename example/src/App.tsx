import { PushPushGo } from '@pushpushgo/react-native-sdk';
import { useCallback, useEffect, useState } from 'react';
import { Text, View, StyleSheet, Button } from 'react-native';

export default function App() {
  const [id, setId] = useState<string | null>(null);

  useEffect(() => {
    const _setSubscriberId = () => {
      PushPushGo.getSubscriberId()
        .then((_id) => setId(_id))
        .catch();
    };

    _setSubscriberId();

    const intervalId = setInterval(_setSubscriberId, 1000);

    return () => clearInterval(intervalId);
  }, []);

  const onRegisterToNotifications = useCallback(() => {
    PushPushGo.subscribeToNotifications().then((_id) => setId(_id));
  }, []);

  const onUnregisterFromNotifications = useCallback(() => {
    PushPushGo.unsubscribeFromNotifications();
  }, []);

  return (
    <View style={styles.container}>
      <View style={styles.textContainer}>
        <Text style={styles.text}>PushPushGo SDK</Text>
        <Text style={styles.text}>Subscriber ID: {id ?? '?'}</Text>
      </View>
      <View style={styles.buttonsContainer}>
        <Button
          title="Register To Notifications"
          onPress={onRegisterToNotifications}
        />
        <Button
          title="Unregister from Notification"
          onPress={onUnregisterFromNotifications}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 48,
  },
  text: {
    fontSize: 24,
    textAlign: 'center',
  },
  textContainer: {
    gap: 8,
  },
  buttonsContainer: {
    gap: 16,
  },
});
