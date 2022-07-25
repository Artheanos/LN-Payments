import { Box, Text } from 'native-base'
import React from 'react'

interface DetailsElement {
  title: string
  data: any
}

export function NotificationDetailsElement(props: DetailsElement) {
  return (
    <Box alignItems="center">
      <Text fontSize={20} fontWeight="bold">
        {props.title}
      </Text>
      <Text italic fontSize={17}>
        {props.data}
      </Text>
    </Box>
  )
}
