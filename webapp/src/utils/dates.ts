export const millisecondsToClock = (milliseconds: number) => {
  return new Date(milliseconds).toISOString().substring(14, 19)
}

export const secondsFromNow = (seconds: number) => {
  const result = new Date()
  result.setSeconds(result.getSeconds() + seconds)
  return result
}
