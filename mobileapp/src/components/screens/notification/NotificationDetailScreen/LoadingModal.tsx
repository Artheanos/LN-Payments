import React from 'react'
import R from 'res/R'
import { Modal, Spinner, Text } from 'native-base'

type Props = {
  processing: boolean
}

/**
 * Component displayed while backend is processing the notification requests.
 *
 * @param processing  Current flag value. Determines if modal should be displayed.
 */
export const LoadingModal: React.FC<Props> = ({ processing }) => {
  return (
    <Modal isOpen={processing}>
      <Modal.Content>
        <Modal.Body>
          <Text fontWeight={'bold'}>{R.strings.details.loading.text}</Text>
          <Spinner paddingY={16} size={'lg'} />
        </Modal.Body>
      </Modal.Content>
    </Modal>
  )
}
