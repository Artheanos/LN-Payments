import { StatusBar } from 'expo-status-bar'
import React, { useState } from 'react'
import {
  Button,
  StyleSheet,
  Text,
  TextInput,
  View,
  TouchableOpacity,
} from 'react-native'

export default function App() {
  const [counter, setCounter] = useState(0)
  const [minValue, setMinValue] = useState(0)
  const [maxValue, setMaxValue] = useState(0)

  return (
    <View style={styles.container}>
      <Text>Open up App.tsx to start working on your app!!!</Text>
      <StatusBar style="auto" />
      <TextInput
        placeholder="Min"
        onChangeText={(e) => setMinValue(+e)}
        value={minValue.toString()}
      />
      <TextInput
        placeholder="Max"
        onChangeText={(e) => setMaxValue(+e)}
        value={maxValue.toString()}
      />
      <TouchableOpacity onLongPress={() => alert('Easter egg')}>
        <Text style={styles.counter}>{counter}</Text>
      </TouchableOpacity>
      <Button
        title={'+1'}
        onPress={() => {
          setCounter(
            ((Math.random() * (maxValue + 1 - minValue)) >> 0) + minValue,
          )
        }}
      >
        <Text>Hello</Text>
      </Button>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fcf',
    alignItems: 'center',
    justifyContent: 'center',
  },
  counter: {
    fontWeight: 'bold',
    fontSize: 100,
  },
})
