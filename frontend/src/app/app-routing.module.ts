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
import {EventCreateComponent} from "./components/admin/event-create/event-create.component";
import {NewsDetailComponent} from "./components/news-detail/news-detail.component";
import {NewsComponent} from "./components/news/news.component";
import {MerchandiseCreateComponent} from "./components/admin/merchandise-create/merchandise-create.component";
import {MerchandiseComponent} from "./components/merchandise/merchandise.component";
import {CartComponent} from "./components/cart/cart.component";
import {SearchComponent} from "./components/search/search.component";
import {SeatingPlanAComponent} from "./components/seating-plan-A/seating-plan-A.component";
import {SeatingPlanBComponent} from "./components/seating-plan-B/seating-plan-B.component";
import {OrderOverviewComponent} from "./components/order-overview/order-overview.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'cart/:id', canActivate: mapToCanActivate([AuthGuard]), component: CartComponent},
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {path: 'merchandise', canActivate: mapToCanActivate([AuthGuard]), component: MerchandiseComponent},
  {path: 'seatingPlanA', component: SeatingPlanAComponent},
  {path: 'seatingPlanB', component: SeatingPlanBComponent},
  {path: 'search', component: SearchComponent},
  {path: 'news', component: NewsComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {path: 'news/details/:id', component: NewsDetailComponent },
  {path: 'order-overview', component: OrderOverviewComponent },
  {path: '', children: [
      {path: 'admin', canActivate: mapToCanActivate([AuthGuard, AdminGuard]), component: AdminComponent},
      {path: 'admin/createUser', canActivate: mapToCanActivate([AuthGuard, AdminGuard]), component: CreateUserComponent},
      {path: 'admin/createEvent', canActivate: mapToCanActivate([AuthGuard, AdminGuard]), component: EventCreateComponent},
      {path: 'admin/createNews', canActivate: mapToCanActivate([AuthGuard, AdminGuard]), component: NewsCreateComponent},
      {path: 'admin/createMerchandise', canActivate: mapToCanActivate([AuthGuard, AdminGuard]), component: MerchandiseCreateComponent}
    ]
  },
  {path: 'home', component: HomeComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

