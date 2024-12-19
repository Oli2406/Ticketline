import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {NewsCreateComponent} from "./components/admin/news-create/news-create.component";
import {RegisterComponent} from './components/register/register.component';
import {AdminGuard} from "./guards/admin.guard";
import {AdminComponent} from './components/admin/admin.component';
import {CreateUserComponent} from './components/admin/createUser/create-user.component';
import {ResetPasswordComponent} from "./components/password-reset/reset-password/reset-password.component";
import {VerifyResetCodeComponent} from "./components/password-reset/verify-reset-code/verify-reset-code.component";
import {SendEmailComponent} from "./components/password-reset/send-email/send-email.component";
import {EventCreateComponent} from "./components/admin/event-create/event-create.component";
import {NewsDetailComponent} from "./components/news-detail/news-detail.component";
import {NewsComponent} from "./components/news/news.component";
import {MerchandiseCreateComponent} from "./components/admin/merchandise-create/merchandise-create.component";
import {MerchandiseComponent} from "./components/merchandise/merchandise.component";
import {CartComponent} from "./components/cart/cart.component";
import {SearchComponent} from "./components/search/search.component";
import {SeatingPlanAComponent} from "./components/seating-plan-A/seating-plan-A.component";
import {SeatingPlanBComponent} from "./components/seating-plan-B/seating-plan-B.component";
import {ArtistComponent} from "./components/artist/artist.component";
import {EventComponent} from "./components/event/event.component";
import {LocationComponent} from "./components/location/location.component";
import {UserAccountComponent} from "./components/user-account/user-account.component";
import {OrderOverviewComponent} from "./components/order-overview/order-overview.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'artist/:id', canActivate: mapToCanActivate([AuthGuard]), component: ArtistComponent},
  {path: 'event/:id', canActivate: mapToCanActivate([AuthGuard]), component: EventComponent},
  {path: 'location/:id', canActivate: mapToCanActivate([AuthGuard]), component: LocationComponent},
  {path: 'cart/:id', canActivate: mapToCanActivate([AuthGuard]), component: CartComponent},
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {path: 'merchandise', canActivate: mapToCanActivate([AuthGuard]), component: MerchandiseComponent},
  {path: 'seatingPlanA', canActivate: mapToCanActivate([AuthGuard]), component: SeatingPlanAComponent},
  {path: 'seatingPlanB', canActivate: mapToCanActivate([AuthGuard]), component: SeatingPlanBComponent},
  {path: 'search', canActivate: mapToCanActivate([AuthGuard]), component: SearchComponent},
  {path: 'news', canActivate: mapToCanActivate([AuthGuard]), component: NewsComponent},
  {path: 'order-overview',canActivate: mapToCanActivate([AuthGuard]), component: OrderOverviewComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {path: 'news/details/:id', component: NewsDetailComponent },
  {path: '', children: [
      {path: 'admin', canActivate: mapToCanActivate([AuthGuard, AdminGuard]), component: AdminComponent},
      {path: 'admin/createUser', canActivate: mapToCanActivate([AuthGuard, AdminGuard]), component: CreateUserComponent},
      {path: 'admin/createEvent', canActivate: mapToCanActivate([AuthGuard, AdminGuard]), component: EventCreateComponent},
      {path: 'admin/createNews', canActivate: mapToCanActivate([AuthGuard, AdminGuard]), component: NewsCreateComponent},
      {path: 'admin/createMerchandise', canActivate: mapToCanActivate([AuthGuard, AdminGuard]), component: MerchandiseCreateComponent}
    ]
  },
  {path: 'reset-password', canActivate: [AuthGuard] ,component: ResetPasswordComponent},
  {path: 'verify-reset-code',  canActivate: [AuthGuard] , component: VerifyResetCodeComponent},
  {path: 'send-email' , component: SendEmailComponent},
  {path: 'user-account', canActivate:[AuthGuard] ,component: UserAccountComponent},
  {path: 'home', component: HomeComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: false})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

