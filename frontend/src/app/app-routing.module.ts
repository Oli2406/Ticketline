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
import {NewsDetailComponent} from "./components/news-detail/news-detail.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'merchandise', component: HomeComponent},
  {path: 'events', component: HomeComponent},
  {path: 'news', component: HomeComponent },
  {path: 'news/details/:id', component: NewsDetailComponent },
  {path: 'register', component: RegisterComponent},
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {
    path: 'admin', component: AdminComponent, canActivate: [AuthGuard, AdminGuard], children: [
      {path: 'createUser', component: CreateUserComponent}
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
