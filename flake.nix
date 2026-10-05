{
  description = "Additional Error Prone checks for tidier Java code";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-parts = {
      url = "github:hercules-ci/flake-parts";
      inputs.nixpkgs-lib.follows = "nixpkgs";
    };
    treefmt-nix = {
      url = "github:numtide/treefmt-nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
    git-hooks = {
      url = "github:cachix/git-hooks.nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs =
    inputs:
    inputs.flake-parts.lib.mkFlake { inherit inputs; } {
      imports = [
        inputs.treefmt-nix.flakeModule
        inputs.git-hooks.flakeModule
      ];

      systems = [
        "aarch64-darwin"
        "aarch64-linux"
        "x86_64-linux"
      ];

      perSystem =
        { config, pkgs, ... }:
        let
          jdk = pkgs.jdk25;
        in
        {
          treefmt = {
            projectRootFile = "flake.nix";
            programs.nixfmt.enable = true;
            programs.google-java-format.enable = true;
          };

          pre-commit.settings.hooks = {
            treefmt = {
              enable = true;
              package = config.treefmt.build.wrapper;
            };

            gitleaks = {
              enable = true;
              name = "gitleaks";
              entry = "${pkgs.gitleaks}/bin/gitleaks git --pre-commit --staged --redact --verbose";
              pass_filenames = false;
            };

            convco.enable = true;

            end-of-file-fixer.enable = true;
            trim-trailing-whitespace.enable = true;
            check-merge-conflicts.enable = true;
            check-added-large-files.enable = true;
            detect-private-keys.enable = true;

            typos = {
              enable = true;
              files = "\\.md$";
            };
            markdownlint = {
              enable = true;
              excludes = [ "^CHANGELOG\\.md$" ];
              settings.configuration = {
                default = true;
                MD013 = false;
                MD031.list_items = false;
                MD033 = false;
                MD060 = false;
              };
            };

            actionlint.enable = true;
          };

          devShells.default = pkgs.mkShell {
            packages = [
              jdk
              (pkgs.maven.override { jdk_headless = jdk; })
            ]
            ++ config.pre-commit.settings.enabledPackages;

            JAVA_HOME = jdk.home;

            shellHook = config.pre-commit.installationScript + ''
              export SOURCE_DATE_EPOCH="$(git log -1 --format=%ct)"
            '';
          };
        };
    };
}
