import { getSubscriberId } from '@pushpushgo/react-native-sdk';
import { useEffect, useState } from 'react';
import { Text, View, StyleSheet } from 'react-native';

export default function App() {
  const [id, setId] = useState<string | null>(null);

  useEffect(() => {
    getSubscriberId()
      .then((_id) => setId(_id))
      .catch();
  }, []);

  return (
    <View style={styles.container}>
      <Text>Hello world</Text>
      <Text>Subscriber ID: {id ?? '?'}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
