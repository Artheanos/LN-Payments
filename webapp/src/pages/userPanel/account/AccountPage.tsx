import React, { useEffect, useState } from 'react'
import Panel from 'components/common/Panel'
import { api } from 'webService/requests'
import { User } from 'webService/interface/user'
import { Alert, Button, CircularProgress, Grid, TextField } from '@mui/material'
import { Link } from 'react-router-dom'
import routesBuilder from 'routesBuilder'
import { useTranslation } from 'react-i18next'

export const AccountPage: React.FC = () => {
  const [user, setUser] = useState<User>()
  const [loading, setLoading] = useState(true)
  const { t } = useTranslation('account')

  const getUserDetails = () => {
    setLoading(true)
    api.users
      .getUserDetails()
      .then(({ data }) => {
        if (data) {
          setUser(data)
        } else {
          setLoading(false)
        }
      })
      .catch(() => {
        setLoading(false)
      })
  }

  useEffect(() => {
    getUserDetails()
  }, [])

  useEffect(() => {
    if (loading && user) {
      setLoading(false)
    }
  }, [user, loading])

  const AccountRow: React.FC<{ name: string; value: string }> = ({
    name,
    value
  }) => (
    <Grid xs={10} item>
      <TextField
        id={name}
        label={name}
        className="w-full"
        defaultValue={value}
        variant="standard"
        InputProps={{
          readOnly: true
        }}
      />
    </Grid>
  )

  return (
    <Panel.Container>
      <Panel.Header title={t('header')} />
      <Panel.Body>
        {loading ? (
          <div className="text-center">
            <CircularProgress />
          </div>
        ) : !user ? (
          <div className="text-center">
            <p className="pb-10 italic text-gray-500">
              {t('common:error.message')}
            </p>
          </div>
        ) : (
          <Grid container>
            <Grid item xs={12} md={6} container gap={3}>
              <AccountRow name={t('common:name')} value={user!.fullName} />
              <AccountRow name={t('common:email')} value={user!.email} />
              <AccountRow name={t('common:role')} value={user!.role} />
              <AccountRow
                name={t('creationDate')}
                value={new Date(user!.createdAt!).toLocaleDateString('pl-PL')}
              />
            </Grid>
            <Grid
              item
              xs={12}
              md={6}
              gap={3}
              my={3}
              container
              justifyContent="center"
              alignItems="center"
            >
              <Grid item xs={12}>
                <Link to={routesBuilder.userPanel.account.password}>
                  <Button className="w-full" variant="contained">
                    {t('changePassword')}
                  </Button>
                </Link>
              </Grid>
              <Grid xs={12} item>
                <Alert variant="standard" severity="info">
                  {t('noEditAlert')}
                </Alert>
              </Grid>
            </Grid>
          </Grid>
        )}
      </Panel.Body>
    </Panel.Container>
  )
}
