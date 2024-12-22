import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketExpirationDialogComponent } from './ticket-expiration-dialog.component';

describe('TicketExpirationDialogComponent', () => {
  let component: TicketExpirationDialogComponent;
  let fixture: ComponentFixture<TicketExpirationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketExpirationDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketExpirationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
