import React, { useContext } from 'react'
import { Box, Heading } from 'native-base'
import { UserContext } from 'components/context/UserContext'

export const MainScreen: React.FC = () => {
  const { user } = useContext(UserContext)

  return (
    <Box>
      <Heading>{JSON.stringify(user.publicKey)}</Heading>
    </Box>
  )
}
