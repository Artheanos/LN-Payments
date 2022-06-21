import React from 'react'

import { Box, Button, Center, Heading } from 'native-base'
import Icon from 'react-native-vector-icons/MaterialIcons'
import R from 'res/R'
import { StackScreenProps } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'

/**
 * Screen that is displayed on success of notification processing flow. Background color and content
 * is determined on the taken action.
 *
 * @param navigation  Navigation prop used for redirection. Obtained from router.
 * @param isConfirmation  Flag determining which process was executed.
 */
export const NotificationResultScreen: React.FC<
  StackScreenProps<SignInRouterProps, typeof R.routes.outcome>
> = ({
  navigation,
  route: {
    params: { isConfirmation },
  },
}) => {
  const bgColor = isConfirmation ? R.colors.green : R.colors.red
  const icon = isConfirmation ? 'done' : 'close'
  const text = isConfirmation
    ? R.strings.details.result.confirmationText
    : R.strings.details.result.denialText

  return (
    <Box backgroundColor={bgColor} h={'100%'} padding={5}>
      <Center marginTop={'auto'} marginBottom={'auto'}>
        <Icon name={icon} color={R.colors.white} size={200}></Icon>
        <Heading color={'primary.50'} w="80%" textAlign={'center'}>
          {text}
        </Heading>
      </Center>
      <Button
        w={'100%'}
        background={'primary.50'}
        _text={{ color: 'darkText' }}
        onPress={() => navigation.navigate(R.routes.drawer)}
      >
        {R.strings.common.ok}
      </Button>
    </Box>
  )
}
