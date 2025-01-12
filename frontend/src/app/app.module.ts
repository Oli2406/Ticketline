import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {AdminComponent} from './components/admin/admin.component';
import {CreateUserComponent} from './components/admin/createUser/create-user.component';
import {
  ResetPasswordComponent
} from "./components/password-reset/reset-password/reset-password.component";
import {
  VerifyResetCodeComponent
} from "./components/password-reset/verify-reset-code/verify-reset-code.component";
import {SendEmailComponent} from "./components/password-reset/send-email/send-email.component";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';
import {SeatingPlanAComponent} from "./components/seating-plan-A/seating-plan-A.component";
import {SeatingPlanBComponent} from "./components/seating-plan-B/seating-plan-B.component";
import {UserAccountComponent} from "./components/user-account/user-account.component";
import {ConfirmDialogComponent} from "./components/confirm-dialog/confirm-dialog.component";

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    AdminComponent,
    CreateUserComponent,
    ResetPasswordComponent,
    VerifyResetCodeComponent,
    SendEmailComponent,
    SeatingPlanAComponent,
    SeatingPlanBComponent,
    UserAccountComponent,
  ],
  bootstrap: [AppComponent],
  imports: [BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    NgbModule,
    FormsModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot({
      enableHtml: true,
      timeOut: 5000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
      closeButton: true,
      progressBar: true,
      easing: 'ease-in-out',
      easeTime: 300,
    }), ConfirmDialogComponent
  ],
  providers: [httpInterceptorProviders, provideHttpClient(withInterceptorsFromDi())]
})
  export class AppModule {
  }
