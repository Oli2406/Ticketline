import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {RegisterComponent} from './components/register/register.component';
import {AdminGuard} from "./guards/admin.guard";
import {AdminComponent} from './components/admin/admin.component';
import {CreateUserComponent} from './components/admin/createUser/create-user.component';
import {ResetPasswordComponent} from "./components/password-reset/reset-password/reset-password.component";
import {VerifyResetCodeComponent} from "./components/password-reset/verify-reset-code/verify-reset-code.component";
import {SendEmailComponent} from "./components/password-reset/send-email/send-email.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'merchandise', component: HomeComponent},
  {path: 'events', component: HomeComponent},
  {path: 'news', component: HomeComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {
    path: 'admin', component: AdminComponent, canActivate: [AuthGuard, AdminGuard], children: [
      {path: 'createUser', component: CreateUserComponent}
    ]
  },
  {path: 'reset-password', canActivate: [AuthGuard] ,component: ResetPasswordComponent},
  {path: 'verify-reset-code',  canActivate: [AuthGuard] , component: VerifyResetCodeComponent},
  {path: 'send-email' , component: SendEmailComponent},
  {path: 'home', component: HomeComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: false})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
