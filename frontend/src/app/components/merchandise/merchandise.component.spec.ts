import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MerchandiseComponent } from './merchandise.component';

describe('MerchandiseComponent', () => {
  let component: MerchandiseComponent;
  let fixture: ComponentFixture<MerchandiseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MerchandiseComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MerchandiseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
