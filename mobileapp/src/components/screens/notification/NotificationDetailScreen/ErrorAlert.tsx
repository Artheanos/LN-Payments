import React, { useRef } from 'react'
import R from 'res/R'
import { AlertDialog, Button } from 'native-base'

type Props = {
  isOpen: boolean
  close: () => void
}

/**
 * Alert that is displayed when notification handling process fails.
 *
 * @param isOpen  Flag responsible for showing the alert.
 * @param close  Method that closes the alert.
 */
export const ErrorAlert: React.FC<Props> = ({ isOpen, close }) => {
  const cancelRef = useRef()

  return (
    <AlertDialog
      leastDestructiveRef={cancelRef}
      isOpen={isOpen}
      onClose={close}
    >
      <AlertDialog.Content>
        <AlertDialog.CloseButton />
        <AlertDialog.Header fontSize="lg" fontWeight="bold">
          {R.strings.details.error.header}
        </AlertDialog.Header>
        <AlertDialog.Body>{R.strings.details.error.text}</AlertDialog.Body>
        <AlertDialog.Footer>
          <Button onPress={close}>{R.strings.common.ok}</Button>
        </AlertDialog.Footer>
      </AlertDialog.Content>
    </AlertDialog>
  )
}
