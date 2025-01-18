import { Component } from '@angular/core';
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { MerchandiseService } from "../../../services/merchandise.service";
import { ToastrService } from "ngx-toastr";
import { Router } from "@angular/router";
import {AdminService} from "../../../services/admin.service";

@Component({
  selector: 'app-merchandise-create',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
  ],
  templateUrl: './merchandise-create.component.html',
  styleUrls: ['./merchandise-create.component.scss']
})
export class MerchandiseCreateComponent {
  merchandiseData = {
    name: '',
    category: '',
    stock: 0,
    price: null,
    points: 0,
  };
  imageFile: File | null = null;
  imagePreview: string | null = null;

  constructor(
    private merchandiseService: MerchandiseService,
    private toastr: ToastrService,
    private router: Router,
  ) {}

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.imageFile = file;

      this.imagePreview = URL.createObjectURL(file);
    } else {
      this.imageFile = null;
      this.imagePreview = null;
    }
  }


  onSubmit(): void {
    this.merchandiseService.createMerchandise(this.merchandiseData, this.imageFile).subscribe({
      next: () => {
        this.toastr.success("Merchandise created successfully!", "Success");
        this.resetForm();
        this.router.navigate(['/admin']);
      },
      error: (err) => {
        console.error('Error occurred:', err.message);
        const errors = Array.isArray(err.message)
          ? err.message
          : err.message.split(/\n/);
        const errorList = errors
          .map((error) => `<li>${error.trim()}</li>`)
          .join('');
        this.toastr.error(`<ul>${errorList}</ul>`, "Failed to create merchandise", {
          enableHtml: true,
        });
      },
    });
  }


  resetForm(): void {
    this.merchandiseData = {
      name: '',
      category: '',
      stock: 0,
      price: null,
      points: 0
    };
    this.imageFile = null;
    this.imagePreview = null;
  }
}
