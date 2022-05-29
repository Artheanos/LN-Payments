
// type Color = {
//   primary: string
//   secondary: string
// }
//
// type Props = {
//   statusColors: StatusColors
//   status: string
// }
//
// const calculateColor = (status: string, statusColors: unknown) => {
//   const colors = statusColors[paymentStatus]
//   return {
//     backgroundColor: colors.primary,
//     borderColor: colors.secondary
//   }
// }
//
// export const StatusCell = <T,>({ calculateColor, status }: Props<T>) => {
//   return (
//     <Typography
//       align="center"
//       sx={{
//         borderRadius: 8,
//         fontWeight: 'bold',
//         display: 'inline-block',
//         fontSize: '0.75rem',
//         padding: '3px 10px',
//         borderStyle: 'solid',
//         borderWidth: '3px',
//         width: '100%',
//         ...(calculateColor(status) as object)
//       }}
//     >
//       {status}
//     </Typography>
//   )
// }
