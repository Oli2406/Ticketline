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
import {MerchandiseCreateComponent} from "./components/admin/merchandise-create/merchandise-create.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {path: 'merchandise', component: HomeComponent},
  {path: 'events', component: HomeComponent},
  {path: 'news', component: HomeComponent},
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
  {path: 'home', component: HomeComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
